import re
import glob
import openpyxl
from openpyxl.chart import BarChart, Reference
from openpyxl.chart.series import SeriesLabel
from openpyxl.utils import get_column_letter

MD_PATH = r"C:\Users\Mário Zoio\OneDrive\Documentos\DSOFS\projeto_desofs\desofs2026_thu_ffs_5\Deliverables\Phase1\ASVS_Checklist\ASVS_5.0_Tracker.md"
XLSX_PATH = glob.glob(r"C:\Users\**\ASVS_5.0_Tracker1.xlsx", recursive=True)[0]

# Map .md statuses to xlsx statuses expected by COUNTIFS("Compliant")
STATUS_MAP = {
    "Met": "Compliant",
    "Planned": "Planned",
    "N/A": "N/A",
    "Not Started": "Not Started",
}

def parse_md(path):
    """Parse all requirement rows from the .md file. Returns dict keyed by Req ID."""
    data = {}
    with open(path, encoding="utf-8") as f:
        for line in f:
            line = line.rstrip()
            if not line.startswith("|"):
                continue
            parts = [p.strip() for p in line.split("|")]
            parts = [p for p in parts if p != ""]
            if len(parts) < 8:
                continue
            req_id = parts[2].strip()
            if not re.match(r"^V\d+\.\d+\.\d+$", req_id):
                continue
            raw_status = parts[5].strip()
            status = STATUS_MAP.get(raw_status, raw_status)
            observations = parts[6].strip()
            reference = parts[7].strip()
            data[req_id] = {
                "status": status,
                "observations": observations,
                "reference": reference,
            }
    return data

def fill_xlsx(xlsx_path, data):
    wb = openpyxl.load_workbook(xlsx_path)
    updated = 0
    for sheet_name in wb.sheetnames:
        if sheet_name == "Summary":
            continue
        ws = wb[sheet_name]
        header = [cell.value for cell in ws[1]]
        try:
            col_req_id = header.index("Req ID") + 1
            col_status = header.index("Status") + 1
            col_obs = header.index("Observations") + 1
            col_ref = header.index("Reference / Link") + 1
        except ValueError:
            print(f"  Skipping sheet '{sheet_name}' - missing expected columns")
            continue

        for row in ws.iter_rows(min_row=2):
            req_id_cell = row[col_req_id - 1]
            req_id = req_id_cell.value
            if not req_id or not re.match(r"^V\d+\.\d+\.\d+$", str(req_id)):
                continue
            if req_id in data:
                row[col_status - 1].value = data[req_id]["status"]
                row[col_obs - 1].value = data[req_id]["observations"]
                row[col_ref - 1].value = data[req_id]["reference"]
                updated += 1
            else:
                print(f"  No match found for {req_id} in sheet '{sheet_name}'")

    print(f"  Updated {updated} requirements across chapter sheets.")
    rebuild_summary_chart(wb)
    wb.save(xlsx_path)
    print(f"Saved: {xlsx_path}")

def rebuild_summary_chart(wb):
    """Remove the broken chart and create a correct clustered bar chart on Summary."""
    ws = wb["Summary"]

    # Remove all existing charts
    ws._charts.clear()

    # Data rows: 6-22 (17 chapters), header in row 5
    # Columns in Summary:
    #   A=Chapter, B=Total Reqs,
    #   C=L1 Total, D=L1 Compliant, E=L1%,
    #   F=L2 Total, G=L2 Compliant, H=L2%,
    #   I=L3 Total, J=L3 Compliant, K=L3%,
    #   L=Overall Compliant, M=Overall%
    DATA_MIN_ROW = 6
    DATA_MAX_ROW = 22  # row 23 is TOTAL

    chart = BarChart()
    chart.type = "col"          # vertical column chart
    chart.grouping = "clustered"
    chart.title = "ASVS 5.0 Compliance by Chapter"
    chart.y_axis.title = "Compliant Requirements"
    chart.x_axis.title = "Chapter"
    chart.style = 10
    chart.width = 30
    chart.height = 18

    # Series: L1 Compliant (col D=4), L2 Compliant (col G=7), L3 Compliant (col J=10), Overall (col L=12)
    series_defs = [
        (4,  "L1 Compliant"),
        (7,  "L2 Compliant"),
        (10, "L3 Compliant"),
        (12, "Overall Compliant"),
    ]

    for col_idx, series_title in series_defs:
        values = Reference(ws, min_col=col_idx, min_row=DATA_MIN_ROW,
                           max_row=DATA_MAX_ROW)
        series = openpyxl.chart.Series(values, title=series_title)
        # Skip cells with "N/A" gracefully - openpyxl will treat non-numeric as gap
        chart.append(series)

    # Category labels: column A (chapter names)
    cats = Reference(ws, min_col=1, min_row=DATA_MIN_ROW, max_row=DATA_MAX_ROW)
    chart.set_categories(cats)

    chart.shape = 4
    ws.add_chart(chart, "O5")
    print("  Summary chart rebuilt at O5.")

if __name__ == "__main__":
    print(f"Using file: {XLSX_PATH}")
    print("Parsing .md file...")
    data = parse_md(MD_PATH)
    print(f"Found {len(data)} requirements in .md file.")
    print("Filling chapter sheets and rebuilding chart...")
    fill_xlsx(XLSX_PATH, data)
    print("\nDone.")
