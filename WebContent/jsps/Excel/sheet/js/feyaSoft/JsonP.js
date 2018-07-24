/**
 * FeyaSoft EnterpriseSheet 
 * Copyright(c) 2007-2013, FeyaSoft Inc. All right reserved. 
 * info@feyasoft.com http://www.feyasoft.com
 */
Ext.ns("feyaSoft.api");

feyaSoft.api.JsonP = function(){
	
	// As documented ... parameters
	// c indicates the company id; 
	// f indicates the filter (FIN, MUL, MKT, META); 
	// v indicates the type of result (ABSV, PCHG, CAGR). Default is 'ABSV'; 
	// params is an array of other parameters are based on the requirements of the filter
	// 
	// And this is output
	/**
		count indicates the number of records in the data set
		items is an array with the formula result
		formula is the formula string
		params is an array with the input parameters
		result is the computed value
		Examples
		
		GET https://api.securities.com/v2/metrics/isif/compute?f=META&c=4738&v=ABSV&params[0]=name
		http://emisweb03.securities.com/php/api/v2/formulae/isif/compute?c=3303906&f=INF&v=ABSV&params[0]=name
		{
		  "status":200,
		  "message":"OK",
		  "apiVersion":"2.0",
		  "data":{
		     "count":1,
		     "total_records":"1",
		     "items":[
		        {
		           "formula":"=ISIF('4738', 'META', 'ABSV', 'name')",
		           "params":[
		              {            
		               "0":"4738",
		               "1":"META",
		               "2":"ABSV",
		               "3":"name"
		              }
		            ],
		           "result":"Hindustan Unilever Ltd.",
		         }
		     ]
		  }
		}
	 */
	var urls = {
	    // example: =ISIF('3303906', 'INF', 'ABSV', 'name')
		'ISIFFormulaUrl': 'http://emisweb03.securities.com/php/api/v2/formulae/isif/compute?',	
		//'ISIFFormulaUrl': 'http://api-dev.securities.com/v2/formulae/isif/compute?'
		'ISIFCompanyUrl': 'http://api-dev.securities.com/v2/companies?'
	};
	
	
	var callISIFormula = function(formula, params, callback, scope){
	    var url = null;
	    var readResponse = function(action, response){		
		return response;
	    };
	
	    var reader = {
		readResponse: readResponse 
	    };

		// check whether this is for ISIF formula
		if (formula == "ISIF") {
			url = urls['ISIFFormulaUrl'];
			var ISFArgs = params.ISFArgs;	
			var length = ISFArgs.length;
			
			if (length == 1)  url = url + "c=" + ISFArgs[0]; 
			else if (length == 2)  url = url + "c=" + ISFArgs[0] + "&f=" + ISFArgs[1]; 
			else if (length == 3)  url = url + "c=" + ISFArgs[0] + "&f=" + ISFArgs[1] + "&v=" + ISFArgs[2];
			else if (length > 3) {
				url = url + "c=" + ISFArgs[0] + "&f=" + ISFArgs[1] + "&v=" + ISFArgs[2];
				for (var i=3; i<length; i++) {
					var startPoint = i - 3;
					url = url + "&params[" + startPoint + "]=" + ISFArgs[i];
				}
			}
		} 
		
		if (url) {	
		    // This is EXTJS format ... jsonP call
			var proxy = new Ext.data.ScriptTagProxy({url: url});
			proxy.doRequest(undefined, [], [], reader, callback, scope);
			
			// This is JQuery format ... jsonP call
			/**
			$.ajax({
				url: url,
				type: "GET",
				dataType: "jsonP",
				success: function(response) {
					callback(scope, response);
				}
			});
			**/
		}
	};
	
	var searchCompanyName = function(keyword, callback, scope, limit){
		var url = urls['ISIFCompanyUrl'];
		limit = limit || 15;
		url += 'q=[name:'+keyword+'*]&max-results='+limit;
		
		var readResponse = function(action, response){		
			return response;
		};
		
		var reader = {
			readResponse: readResponse 
		};
		
		var proxy = new Ext.data.ScriptTagProxy({url: url});
		proxy.doRequest(undefined, [], [], reader, callback, scope);
	};
	
	return {
		callISIFormula: callISIFormula,
		searchCompanyName : searchCompanyName
	};
}();