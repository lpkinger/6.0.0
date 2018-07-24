/**
 * This is function for submit survey 
 */
function SURVEY_SUBMIT_DATA_CALLBACK_FN() {
	// first we need check whether there has any comments, if not, popup error msg.
	var listVariables = SHEET_API.getCellVariables(SHEET_API_HD);
	var comments = listVariables["customerComments"];
	if (comments == null || comments.length == 0) {
		SHEET_API.setFocus(SHEET_API_HD, 5, 2); 
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
	
	Ext.Msg.alert("Success", "Collected data: <br/> " + jsonString);
	
	/**
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
    **/   
}

//function to add filter to the list
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

//function to remove filter
function BTN_REMOVE_FILTER_CALLBACK_FN() {
	SHEET_API.removeFilter(SHEET_API_HD, SHEET_API_HD.sheet.getSheetId(), false);
}

