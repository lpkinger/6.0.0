/**
 * This is function query customer existing data ...
 */
function AUTO_FILL_CUSTOMER_DATA_BY_EMPLOYEEID(employeeId, sheetId, row, col) {
	
	// ok this function can call your backend and query data for employeeId and fill to here
	// In here, we just simulation it ...	
	var cells = [];
	cells.push({sheet: sheetId, row: row, col: 2, json: {data: 'John' + row + " Doe"}});	
	cells.push({sheet: sheetId, row: row, col: 3, json: {render:'dropRender', data: 'HR Dept', dropId: 1}});
	cells.push({sheet: sheetId, row: row, col: 4, json: {data: 'John' + row + '@yourCompany.com'}});
	cells.push({sheet: sheetId, row: row, col: 5, json: {data: '1 (888) 123-4567'}});
	cells.push({sheet: sheetId, row: row, col: 6, json: {data: 'Female'}});
	cells.push({sheet: sheetId, row: row, col: 7, json: {data: '1990-08-20', fm: "date", dfm: "F d, Y"}});
	cells.push({sheet: sheetId, row: row, col: 11, json: {data: 102123.34567}});
	cells.push({sheet: sheetId, row: row, col: 12, json: {data: 0.99995}});
	SHEET_API.updateCells(SHEET_API_HD, cells);   
}



