/**
 * This function will be called on cell blur action.
 * You need define the following data structure in your json data:
 * 
 * {sheet: 1, row: 3, col: 2, json: {data: "call back onCellBlur", onCellBlurFn: "CELL_ON_BLUR_CALLBACK_FN"}}
 * 
 * The callback will return back 3 parameters: 
 *      cell value, cell row number and cell column number
 */
function CELL_ON_BLUR_CALLBACK_FN(value, row, column, sheetId, cellObj, store) {
    alert("Cell value: " + value + "; Row: " + row + "; Colum: " + column);
};

/**
 * This function will be called on cell FOCUS action.
 * You need define the following data structure in your json data:
 * 
 * {sheet: 1, row: 3, col: 2, json: {data: "call back onCellBlur", onCellFocusFn: "CELL_ON_FOCUS_CALLBACK_FN"}}
 * 
 * The callback will return back 3 parameters: 
 *      cell value, cell row number and cell column number
 */
function CELL_ON_FOCUS_CALLBACK_FN(value, row, column) {
    alert("Cell value: " + value + "; Row: " + row + "; Colum: " + column);
};

/**
 * This function will be called on cell click action.
 * You need define the following data structure in your json data:
 * 
 * {sheet: 1, row: 3, col: 2, json: {data: "call back onCellClick", onCellClickFn: "CELL_CLICK_CALLBACK_FN"}}
 * 
 * The callback will return back 3 parameters: 
 *      cell value, cell row number and cell column number
 */
function CELL_CLICK_CALLBACK_FN(value, row, column) {
    alert("Cell value: " + value + "; Row: " + row + "; Colum: " + column);
};

/**
 * This function will be called on cell double click action.
 * You need define the following data structure in your json data:
 * 
 * {sheet: 1, row: 3, col: 2, json: {data: "call back onCellDoubleClick", onCellDoubleClickFn: "CELL_DOUBLE_CLICK_CALLBACK_FN"}}
 * 
 * The callback will return back 3 parameters: 
 *      cell value, cell row number and cell column number
 */
function CELL_DOUBLE_CLICK_CALLBACK_FN(value, row, column) {
    alert("Cell value: " + value + "; Row: " + row + "; Colum: " + column);
};

/**
 * This function will be called on cell mousemove action.
 * You need define the following data structure in your json data:
 * 
 * {sheet: 1, row: 3, col: 2, json: {data: "call back onCellMouseMove", onCellMouseMoveFn: "CELL_MOUSE_MOVE_CALLBACK_FN"}}
 * 
 * The callback will return back 3 parameters: 
 *      cell value, cell row number and cell column number
 */
function CELL_MOUSE_MOVE_CALLBACK_FN(value, row, column) {
    alert("Cell value: " + value + "; Row: " + row + "; Colum: " + column);
};

/**
 * This function will be called on cell mouse down action.
 * You need define the following data structure in your json data:
 * 
 * {sheet: 1, row: 3, col: 2, json: {data: "call back onCellMouseDown", onCellMouseDownFn: "CELL_MOUSE_DOWN_CALLBACK_FN"}}
 * 
 * The callback will return back 3 parameters: 
 *      cell value, cell row number and cell column number
 */
function CELL_MOUSE_DOWN_CALLBACK_FN(value, row, column) {
    alert("Cell value: " + value + "; Row: " + row + "; Colum: " + column);
};

/**
 * @Deprecated
 * 
 * This function will be called on row or column group toggle event.
 * We need define some group - not all group need callback this.
 * 
 * The callback will return back 4 parameters: 
 *      type (row or column), min value, max value, isExpand (true or false)
 */
function ROW_COL_GROUP_TOGGLE_CALLBACK_FN(type, min, max, isExpand) {
    // alert("Group type: " + type + "; Minval: " + min + "; Maxval: " + max + "; isExpand: " + isExpand);
};

// =========================================================================================================

/**
 * This is custom defined function for loading extra data for the custom cell.
 * The returned result is a html page which will be displayed in the popup window
 * during mouse move over.
 * @param {Object} jsonData: the json data in the cell
 * @param {Integer} row: the row index for the cell
 * @param {Integer} column: the column index for the cell
 * @return {String}: the html string to be displayed during mouse move over
 */
function CUSTOM_BINDING_DATA_FN(jsonData, row, column) {
	var html = "Not result found", id = jsonData.id;
	if (jsonData.data.lastIndexOf("Apple") === 0) html = '<table><tr><td><img src="http://iconizer.net/files/WPZOOM_Social_Networking_Icon_Set/orig/apple.png"></td><td valign="top"><b>Apple Inc.</b><br/>1 Infinite Loop<br/>Cupertino, CA 95014<br/>Id: ' + id + '</td></tr></table>';
	if (jsonData.data.lastIndexOf("Google") === 0) html = '<table><tr><td><img src="http://www.google.com/homepage/images/google_favicon_64.png"></td><td valign="top"><b>Google Inc.</b><br/>1600 Amphitheatre Parkway<br/>Mountain View, CA 94043<br/>Id: ' + id + '</td></tr></table>';		
	if (jsonData.data.lastIndexOf("Taylor") === 0) html = '<table><tr><td><img src="http://38.media.tumblr.com/avatar_d4bf27a2bc43_64.png"></td><td valign="top"><b>Tayor Swift</b><br/>December 13, 1989<br/>West Reading, Pennsylvania, USA<br/>Id: ' + id + '</td></tr></table>';

    return html;
}

/**
 * This is custom defined function to popup custom editor for data communicate.
 * @param {Object} jsonData: the json data in the cell
 * @param {Integer} row: the row index for the cell
 * @param {Integer} column: the column index for the cell
 * @return {String}: the html string to be displayed during mouse move over
 */
function CUSTOM_2WAY_BINDING_DATA_FN(jsonData, row, column) {
	var cellEditor = Ext.create('customer.CellEditor',{
		jsonData: jsonData,
		row: row,
		column: column
	});
	cellEditor.show();
}

/**
 * This function will be called on button click action.
 * You need define the following data structure in your json data:
 * 
 * { sheet: 1, row: 4, col: 2, json: { data: "Click me!", it: "button", btnStyle: "color: #900; font-weight: bold;", onBtnClickFn: "CUSTOM_BUTTON_CLICK_CALLBACK_FN" } },
 * 
 * The callback will return back the following parameters: 
 *      cell value, cell row number, cell column number, sheetId, cellObj and store
 */
function CUSTOM_BUTTON_CLICK_CALLBACK_FN(value, row, column, sheetId, cellObj, store) {
	var dataVal = "CUSTOM_BUTTON_CLICK_CALLBACK_FN is called and cell button is clicked @ Row: " + row + "; Colum: " + column;
	var cells = [{ sheet: sheetId, row: 12, col: 2, json: { data: dataVal}}]; 
	SHEET_API.updateCells(SHEET_API_HD, cells);
}

// this is example function for the complex data binding example.
// =========================================================================================
function BTN_ADD_COMMENT_CALLBACK_FN(value, row, column, sheetId, cellObj, store) {
	var cells = [
	    {sheet: sheetId, row: 7, col: 2, json: {comment: '7-2', commentEdit: "hide"}},
	    {sheet: sheetId, row: 7, col: 3, json: {comment: '7-3', commentEdit: "hide"}},
	    {sheet: sheetId, row: 7, col: 4, json: {comment: '7-4', commentEdit: "hide"}},
	    {sheet: sheetId, row: 8, col: 2, json: {comment: '8-2', commentEdit: "hide"}},
	    {sheet: sheetId, row: 8, col: 3, json: {comment: '8-3', commentEdit: "hide"}},
	    {sheet: sheetId, row: 8, col: 4, json: {comment: '8-4', commentEdit: "hide"}},
	    {sheet: sheetId, row: 9, col: 2, json: {comment: '9-2', commentEdit: "hide"}},
	    {sheet: sheetId, row: 9, col: 3, json: {comment: '9-3', commentEdit: "hide"}},
	    {sheet: sheetId, row: 9, col: 4, json: {comment: '9-4', commentEdit: "hide"}},
	    {sheet: sheetId, row: 10, col: 2, json: {comment: '10-2', commentEdit: "hide"}},
	    {sheet: sheetId, row: 10, col: 3, json: {comment: '10-3', commentEdit: "hide"}},
	    {sheet: sheetId, row: 10, col: 4, json: {comment: '10-4', commentEdit: "hide"}},
	    {sheet: sheetId, row: 11, col: 2, json: {comment: '11-2', commentEdit: "hide"}},
	    {sheet: sheetId, row: 11, col: 3, json: {comment: '11-3', commentEdit: "hide"}},
	    {sheet: sheetId, row: 11, col: 4, json: {comment: '11-4', commentEdit: "hide"}},
	]; 
	SHEET_API.updateCells(SHEET_API_HD, cells);
}

function BTN_BINDING_DATA_CALLBACK_FN(value, row, column, sheetId, cellObj, store) {
	// get all cells with comments - call API	
	var allCommentCells = SHEET_API.getCellsComment(SHEET_API_HD);
	
	// we can send those data to the server through Ajax call and get data back, we just assume this work.
	var cells = [];
	for (var i=0; i<allCommentCells.length; i++) {
		var temp = allCommentCells[i];
		cells.push({sheet: sheetId, row: temp.x, col: temp.y, json: {data: temp.comment}, applyWay: "apply"});
	}
	
	SHEET_API.updateCells(SHEET_API_HD, cells);
}

function BTN_SUBMIT_DATA_CALLBACK_FN(value, row, column, sheetId, cellObj, store) {
	var allCommentCells = SHEET_API.getCellsComment(SHEET_API_HD);
	var result = null;
	for (var i=0; i<allCommentCells.length; i++) {
		var temp = allCommentCells[i];
		result = result + temp.x + "|" + temp.y + "|" + temp.data + ";"
	}
	alert(result);
}
// ===============================================================================================================

// simulation and get data from binding and load in here ...
function BTN_LOAD_INVOICE_DATA_CALLBACK_FN(value, row, column, sheetId, cellObj, store) {
	var listVariables = SHEET_API.getCellVariables(SHEET_API_HD);
	alert("All variable are: " + Ext.encode(listVariables));
	
	SHEET_API.setValueToVariable(SHEET_API_HD, {
        'company_name': 'Apple Inc.',
        'street_address': '1 Infinite Loop',
        'city_state_zip': 'Cupertino, CA 95014',
        'city_state_zip': 'Cupertino, CA 95014',
        'phone': '1 800-692-7753',
        'bill_to.name': 'John Doe',
        'bill_to.company_name': 'ABC Company',
        'bill_to.phone': '1 613 2222 222',
        'bill_to.email': 'info@abc.com',
        'invoice_no': '20150515001',
        'list.description': ['Service Fee', 'Labor: 5 hours @ $75/hr', 'Tax(10%)'],
        'list.amount': [200.00, 375.00, 57.50]
    });
	
	// clean the defined variable
	SHEET_API.clearAllVariables(SHEET_API_HD);
}

// function to add filter to the list
function BTN_ADD_FILTER_CALLBACK_FN() {
	SHEET_API.addFilter2Span(SHEET_API_HD, [1, 3, 6, 8, 6]);
	SHEET_API.addFilter2Span(SHEET_API_HD, [SHEET_API_HD.sheet.getSheetId(), 3, 6, 0, 7], {
		6 : {
			'type': 'match',
			'values': ['test']
		},
		7 : {
			'type': 'match',
			'values': ['ok']
		}
	}, false);
}

// function to remove filter
function BTN_REMOVE_FILTER_CALLBACK_FN() {
	// SHEET_API.removeFilter(SHEET_API_HD, 1);
	SHEET_API.removeFilter(SHEET_API_HD, SHEET_API_HD.sheet.getSheetId(), false);
}

// ascending sort the cells
function BTN_SORT_ASC_CALLBACK_FN() {
	SHEET_API.sortCellByAsc(SHEET_API_HD, [1, 4, 2, 8, 2]);
}

// Desc sort the cells
function BTN_SORT_DESC_CALLBACK_FN() {
	SHEET_API.sortCellByDesc(SHEET_API_HD, [1, 4, 2, 8, 2]);
}

// ascending sort the cells
function BTN_SORT_ASC_CALLBACK_FN_2() {
	SHEET_API.sortCellByAsc(SHEET_API_HD, [1, 4, 4, 8, 4]);
}

// Desc sort the cells
function BTN_SORT_DESC_CALLBACK_FN_2() {
	SHEET_API.sortCellByDesc(SHEET_API_HD, [1, 4, 4, 8, 4]);
}

/**
 * This is function for subit survey 
 */
function SURVEY_SUBMIT_DATA_CALLBACK_FN() {
	// first we need check whether there has any comments, if not, popup error msg.
	var listVariables = SHEET_API.getCellVariables(SHEET_API_HD);
	var comments = listVariables["customerComments"];
	if (comments == null || comments.length == 0) {
		Ext.Msg.alert("Error", "Please enter some comments, thanks");
		return;
	}
	
	// get all the value of user entered now ..
	var whereLearnES_obj = SHEET_API.getItemValueByName(SHEET_API_HD, 'whereLearnES').itemValue;	
	if (whereLearnES_obj == "Others") {
		var otherVal = SHEET_API.getCellValue(SHEET_API_HD, 1, 9, 4).data;
		whereLearnES_obj = whereLearnES_obj + " - " + otherVal;
	}
	var occupyTitle = SHEET_API.getCellValue(SHEET_API_HD, 1, 12, 2).data;
	var organization = SHEET_API.getCellValue(SHEET_API_HD, 1, 15, 2).data;
	var priceOK_obj = SHEET_API.getItemValueByName(SHEET_API_HD, 'priceOK').itemValue;
	var qualityOK_obj = SHEET_API.getItemValueByName(SHEET_API_HD, 'qualityOK').itemValue;
	var recommandFriend_obj = SHEET_API.getItemValueByName(SHEET_API_HD, 'recommandFriend').itemValue;
	var customerSerive_obj = SHEET_API.getItemValueByName(SHEET_API_HD, 'customerService').itemValue;
	
	var json = {
		whereLearnES: whereLearnES_obj,
		occupyTitle: occupyTitle,
		organization: organization,
		priceOK: priceOK_obj,
		qualityOK: qualityOK_obj,
		recommandFriend: recommandFriend_obj,
		customerService: customerSerive_obj,
		customerComments: comments
	};
	
	var jsonString = Ext.encode(json);
	
	Ext.Ajax.request({
        url: "surveyDemo/injectData",
        params: {
            jsonData: jsonString
        },
        success: function(response, options){
            var json = Ext.decode(response.responseText), success = json.success;
            if(true == success || "true" == success) {
            	var cells = [];
            	cells.push({ sheet: 3, row: 5,  col: 2, json: { data: ""}, applyWay: "apply" });
            	cells.push({ sheet: 3, row: 15,  col: 2, json: { data: "Click this link to see survey result", link: "http://www.enterpriseSheet.com/sheet/sheet?editFileId=" + json.documentId } });
            	SHEET_API.updateCells(SHEET_API_HD, cells);
            	SHEET_API.mergeCellForSpan(SHEET_API_HD, [3, 15, 2, 15, 4]);
            	
                Ext.Msg.alert("Success", "You have successfully submitted data to the server.");
            }
        },
        failure: function(response){
            
        },
        scope: this
    });
	   
}

function GET_DATA_FROM_RANGE_CALLBACK_FN(){
    var store = SHEET_API_HD.store;
    var arr = [];
    /*
     * here pass the coord range you want to get data from, for example Sheet1!B1:C2, will be like [[1, 1, 2, 2, 3]],
     * you can get data from Sheet1!B1:C2 and Sheet2!E4:F8 in one single call, just pass the arr like
     * [[1, 1, 2, 2, 3], [2, 4, 5, 8, 6]]
     * notice in above I just assume the sheet id of Sheet1 and Sheet2 are 1 and 2
     */
    store.walkRange([[1, 2, 2, 4, 6]], function(rec, span, store, id){
        var cd = store.getCellData(rec.data.sheet, rec.data.row, rec.data.col);
        /*
         * we can save the data into an array and insert these data by call updateCells
         */
        arr.push({
            //here we change the sheetId to 2, so it will update to "Another" sheet
            sheet: 2,
            row: rec.data.row,
            col: rec.data.col,
            json: cd,
            applyWay: 'clear'
        });
    }, this);
    /*
     * check more details about this function in SheetAPI.js
     */
    SHEET_API.updateCells(SHEET_API_HD, arr);
}

/*
 * Here we just use fake data the actual formula will be provided by BP.
 * 
 * That formula can return an array contains multi row data or multi col data depending on the dynamicRange floating is 'row' or 'col'
 * For example, an dynamicRange floating object is like: {
            	"name":"CODE",
                "ftype": "dynamicRange",
                "sheet": 1,
                "json": Ext.encode({type: "row", span: [1,5,1,5,5], formula: 'getDynamicRange("report1", "dynRangeCODE", null)'})	                
            }
   The return array of getDynamicRange("report1", "dynRangeCODE", null) will be like this:
   [{
   	1: {data: 'the value in A5'}, 2: {data: 'the value in B5'}, 5: {data: 'the value in E5'}
   }, {
   	1: {data: 'the value in A6'}, 4: {data: 'the value in D6'}
   }]
   every element in the array represents a row, and the member of that row object represent a cell, the key of the row object is the column index of the cell;
   For column type dynamicRange, it's similar, but every element in the array represent a column, and the key of the column object is the row index of the cell 
 * 
 */
function GET_DYNAMIC_FORMULA_FN(formula, obj){
	var type = obj.type, span = obj.span, sheetId = span[0];
	if('row' === type){
		var arr = [];
		for(var i = 0; i < 3; i++){
			var tmp = {};
			for(var j = span[2]; j <= span[4]; j++){
				tmp[j] = {						
					data: Math.round(100*Math.random()),
					bgc: 'yellow'
				};
			}
			arr.push(tmp);
		}
		return arr;
	}else if('col' === type){
		var arr = [];
		for(var i = 0; i < 4; i++){
			var tmp = {};
			for(var j = span[1]; j <= span[3]; j++){
				tmp[j] = {						
					data: Math.round(100*Math.random()),
					bgc: 'pink'
				};
			}
			arr.push(tmp);
		}
		return arr;
	}
}




