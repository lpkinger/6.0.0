/**
 * Enterprise Spreadsheet Solutions
 * Copyright(c) FeyaSoft Inc. All right reserved.
 * info@enterpriseSheet.com
 * http://www.enterpriseSheet.com
 * 
 * Licensed under the EnterpriseSheet Commercial License.
 * http://enterprisesheet.com/license.jsp
 * 
 * You need to have a valid license key to access this file.
 */
Ext.define('enterpriseSheet.demo.JSONDATA', {
	
	singleton : true,
	
	constructor : function(){		
		
		this.callParent(arguments);
		
		Ext.apply(this, {
			
			emptyOneSheetJson : {
				fileName: 'Empty Sheet',
		        sheets: [
	                {name: 'One tab', id: 1, color: 'orange' }
	            ]
			},
			
			emptyTwoSheetJson : {
				fileName: 'Refresh formula',
		        sheets: [
	                {name: 'Sheet1', id: 1},
	                {name: 'Sheet2', id: 2}
	            ],
	            cells: [
	                { sheet:1, row: 4, col: 0, json: {height: 50} },
	                { sheet:1, row: 0, col: 3, json: {width: 150} },
	                { sheet:1, row: 1, col: 1, json: {data: 11} }, 
	                { sheet:1, row: 2, col: 1, json: {data: '=getAbacusValue()'} },
	                { sheet:1, row: 3, col: 1, json: {data: '=getAbacusValue()'} },
	                { sheet:1, row: 5, col: 1, json: {data: '=sum(A1:A3)'} },
	                { sheet:1, row: 7, col: 1, json: {data: '=getAbacusValue()'} },
	                { sheet:1, row: 8, col: 1, json: {data: '=getAbacusValue()'} },
	                { sheet:1, row: 10, col: 1, json: {data: '=A5+A7+A8'} },
	                { sheet:1, row: 12, col: 1, json: {data: '=A10+1'} },
	                { sheet:1, row: 14, col: 1, json: {data: '=sheet2!A1+A12'} },
	                
	                { sheet:2, row: 1, col: 1, json: {data: '=getAbacusValue()'} } 
	            ]
			},

			/**
			 * This is the basic json example to prove the general feature of EnterpriseSheet.
			 */
            featureBasicJson : {
				fileName: 'Basic file',
		        sheets: [
	                {name: 'First', id: 1, color: 'orange' }, {  name: 'Second', id: 2 }
	            ],
		        cells: [
				    { sheet: 1, row: 0, col: 2, json: { width: 172 } },
				    { sheet: 1, row: 0, col: 3, json: { width: 172 } },
				    { sheet: 1, row: 0, col: 4, json: { bgc: "#FBD5B5" } },
				    { sheet: 1, row: 0, col: 5, json: { width: 172 } },
				    { sheet: 1, row: 0, col: 6, json: { width: 172 } },
				    { sheet: 1, row: 1, col: 0, json: { height: 50 } }, 	
				    
				    { sheet: 1, row: 1, col: 2, json: { data: "Basic Examples", fz:16, fw:"bold", va: "bottom"}}, 
				    { sheet: 2, row: 2, col: 2, json: { data: "Number"}}, 
				    { sheet: 1, row: 2, col: 3, json: { data: 110}}, 	    
				    { sheet: 1, row: 3, col: 2, json: { data: 'Comma format' } },
				    { sheet: 1, row: 3, col: 3, json: { data: "12223.45678", fm: "comma" } }, 
				    { sheet: 1, row: 4, col: 2, json: { data: 'Fraction format' } },
				    { sheet: 1, row: 4, col: 3, json: { data: "12.6", fm: "number", dfm: "# ?/?" } }, 
				    { sheet: 1, row: 5, col: 2, json: { data: 'Percent format' } },
				    { sheet: 1, row: 5, col: 3, json: { data: "0.12345", fm: "percent", dfm: "0.00%" } }, 
				    { sheet: 1, row: 6, col: 2, json: { data: 'Science format' } },
				    { sheet: 1, row: 6, col: 3, json: { data: "123.6", fm: "science"} },
				    { sheet: 1, row: 7, col: 2, json: { data: 'Money format' } },
				    { sheet: 1, row: 7, col: 3, json: { data: "123.45678", fm: "money|$|2|none" } }, 
				    { sheet: 1, row: 8, col: 2, json: { data: 'Custom format' } },
				    { sheet: 1, row: 8, col: 3, json: { data: "-12323.45678", fm: "number", dfm: "$#,##0.00;[Red]-$#,##0.00;[Red]ZERO" } }, 
				    
				    { sheet: 1, row: 2, col: 5, json: { data: 'String' } }, 
				    { sheet: 1, row: 2, col: 6, json: { data: 'Ok, this is test' } }, 
				    { sheet: 1, row: 3, col: 5, json: { data: "String Bold" } },
				    { sheet: 1, row: 3, col: 6, json: { data: "bold", fw: "bold" } }, 
				    { sheet: 1, row: 4, col: 5, json: { data: "String Italic" } },
				    { sheet: 1, row: 4, col: 6, json: { data: "Italic", fs: "italic" } }, 
				    { sheet: 1, row: 5, col: 5, json: { data: "String Underline" } },
				    { sheet: 1, row: 5, col: 6, json: { data: "Underline", u: "underline" } }, 
				    { sheet: 1, row: 6, col: 5, json: { data: 'String Color' } },
				    { sheet: 1, row: 6, col: 6, json: { data: 'font color', color: '#FF0000' } }, 
				    { sheet: 1, row: 7, col: 5, json: { data: 'String font' } },
				    { sheet: 1, row: 7, col: 6, json: { data: 'Courier New', ff: 'Courier New' } }, 
				    { sheet: 1, row: 8, col: 5, json: { data: 'Background' } },
				    { sheet: 1, row: 8, col: 6, json: { bgc: '#F79646' } }, 
				    
				    { sheet: 1, row: 10, col: 2, json: { data: 'If formula'} }, 
				    { sheet: 1, row: 10, col: 3, json: { data: '=if(c2>100, ">100","<=100")', cal: true } }, 
				    { sheet: 1, row: 11, col: 2, json: { data: 'Count formula'} }, 
				    { sheet: 1, row: 11, col: 3, json: { data: '=count(B2:C3)', cal: true } }, 
				    { sheet: 1, row: 12, col: 2, json: { data: 'Sum formula'} }, 
				    { sheet: 1, row: 12, col: 3, json: { data: '=sum(1,2,3)', cal: true } }, 
				    { sheet: 1, row: 13, col: 2, json: { data: 'Date Formula with format' } }, 
				    { sheet: 1, row: 13, col: 3, json: { data: '=today()', cal: true, fm: 'date', dfm: 'Y-m-d'}}, 
				    { sheet: 1, row: 14, col: 2, json: { data: 'IF/VLOOKUP formula'} }, 
				    { sheet: 1, row: 14, col: 3, json: { data: '=IF(ISNA(VLOOKUP(C2,$D$2:$D$15,1,FALSE)), "No", "Yes")', cal: true } }, 
					    
				    { sheet: 1, row: 10, col: 5, json: { data: "Hyperlink" } },
				    { sheet: 1, row: 10, col: 6, json: { data: "www.enterpriseSheet.com", link: "www.enterprisesheet.com" } },
				    { sheet: 1, row: 12, col: 5, json: { data: "Add comment" } },
				    { sheet: 1, row: 12, col: 6, json: { data: "See comments", comment: "Great work" } }
			    ]
			},
			
			/**
			 * This is the example Json Data for highlighting cell based on condition rule
			 * See running example @
			 *     EnterpriseSheet Samples / APIs -> Conditional format -> Number condition rule
			 */
			featureConditionHighlightJson : {
				fileName: 'Number condition rule',
		        sheets: [
	                {name: 'Number condition', id: 1, color: 'orange' }
	            ],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,10]" },
	                { sheet:1, name:"merge2", ftype:"meg", json:"[4,2,4,5]" },
	                { sheet:1, name:"merge3", ftype:"meg", json:"[4,7,4,10]" },
	                { sheet:1, name:"merge4", ftype:"meg", json:"[10,2,10,5]" },
	                { sheet:1, name:"merge5", ftype:"meg", json:"[10,7,10,10]" },
	                { sheet:1, name:"merge6", ftype:"meg", json:"[16,2,16,5]" },
	                { sheet:1, name:"merge7", ftype:"meg", json:"[16,7,16,10]" },
	                { sheet:1, name:"condition1", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,5,2,7,5],type:1}],opt:{type:\"greater\",base:150,style:{cbgc:\"rgb(248,105,107)\",ccolor:\"white\"}},id:\"condition1\"}" },
	                { sheet:1, name:"condition2", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,5,7,7,10],type:1}],opt:{type:\"greater\",base:150,style:{ccolor:\"red\"}},id:\"condition2\"}" },
	                { sheet:1, name:"condition3", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,11,2,13,5],type:1}],opt:{type:\"less\",base:150,style:{cbgc:\"orange\",ccolor:\"white\"}},id:\"condition3\"}" },
	                { sheet:1, name:"condition4", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,11,7,13,10],type:1}],opt:{type:\"between\",base:{min:150,max:220},style:{ccolor:\"blue\"}},id:\"condition4\"}" },
	                { sheet:1, name:"condition5", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,17,2,19,5],type:1}],opt:{type:\"equal\",base:150,style:{cbgc:\"blue\",ccolor:\"white\"}},id:\"condition5\"}" },
	                { sheet:1, name:"condition6", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,17,7,19,10],type:1}],opt:{type:\"repeat\",base:0,style:{ccolor:\"orange\"}},id:\"condition6\"}" }                
	            ],
			    cells: [
			         {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
			         {sheet: 1, row: 2, col: 2, json: {data: "Add number conditional format to the cell range", fw:"bold", fz:14 }}, 
			         
			         {sheet: 1, row: 4, col: 2, json: {data: "Red fill if value greater than 150", ff: 'Courier New'}},
			         {sheet: 1, row: 5, col: 2, json: {data:100}},
			         {sheet: 1, row: 5, col: 3, json: {data:200}},
			         {sheet: 1, row: 5, col: 4, json: {data:300}},
			         {sheet: 1, row: 5, col: 5, json: {data:400}},
			         {sheet: 1, row: 6, col: 2, json: {data:120}},
			         {sheet: 1, row: 6, col: 3, json: {data:220}},
			         {sheet: 1, row: 6, col: 4, json: {data:150}},
			         {sheet: 1, row: 6, col: 5, json: {data:500}},
			         {sheet: 1, row: 7, col: 2, json: {data:130}},
			         {sheet: 1, row: 7, col: 3, json: {data:170}},
			         {sheet: 1, row: 7, col: 4, json: {data:110}},
			         {sheet: 1, row: 7, col: 5, json: {data:600}},
			         
			         {sheet: 1, row: 4, col: 7, json: {data: "Red text if value greater than 150", ff: 'Courier New'}},
			         {sheet: 1, row: 5, col: 7, json: {data:100}},
			         {sheet: 1, row: 5, col: 8, json: {data:200}},
			         {sheet: 1, row: 5, col: 9, json: {data:300}},
			         {sheet: 1, row: 5, col: 10, json: {data:400}},
			         {sheet: 1, row: 6, col: 7, json: {data:120}},
			         {sheet: 1, row: 6, col: 8, json: {data:220}},
			         {sheet: 1, row: 6, col: 9, json: {data:150}},
			         {sheet: 1, row: 6, col: 10, json: {data:500}},
			         {sheet: 1, row: 7, col: 7, json: {data:130}},
			         {sheet: 1, row: 7, col: 8, json: {data:170}},
			         {sheet: 1, row: 7, col: 9, json: {data:110}},
			         {sheet: 1, row: 7, col: 10, json: {data:600}},
			         
			         {sheet: 1, row: 10, col: 2, json: {data: "Orange fill if value less 150", ff: 'Courier New'}},
			         {sheet: 1, row: 11, col: 2, json: {data:100}},
			         {sheet: 1, row: 11, col: 3, json: {data:200}},
			         {sheet: 1, row: 11, col: 4, json: {data:300}},
			         {sheet: 1, row: 11, col: 5, json: {data:400}},
			         {sheet: 1, row: 12, col: 2, json: {data:120}},
			         {sheet: 1, row: 12, col: 3, json: {data:220}},
			         {sheet: 1, row: 12, col: 4, json: {data:150}},
			         {sheet: 1, row: 12, col: 5, json: {data:500}},
			         {sheet: 1, row: 13, col: 2, json: {data:130}},
			         {sheet: 1, row: 13, col: 3, json: {data:170}},
			         {sheet: 1, row: 13, col: 4, json: {data:110}},
			         {sheet: 1, row: 13, col: 5, json: {data:600}},
			         
			         {sheet: 1, row: 10, col: 7, json: {data: "Blue text if value between 150 - 220", ff: 'Courier New'}},
			         {sheet: 1, row: 11, col: 7, json: {data:100}},
			         {sheet: 1, row: 11, col: 8, json: {data:200}},
			         {sheet: 1, row: 11, col: 9, json: {data:300}},
			         {sheet: 1, row: 11, col: 10, json: {data:400}},
			         {sheet: 1, row: 12, col: 7, json: {data:120}},
			         {sheet: 1, row: 12, col: 8, json: {data:220}},
			         {sheet: 1, row: 12, col: 9, json: {data:150}},
			         {sheet: 1, row: 12, col: 10, json: {data:500}},
			         {sheet: 1, row: 13, col: 7, json: {data:130}},
			         {sheet: 1, row: 13, col: 8, json: {data:170}},
			         {sheet: 1, row: 13, col: 9, json: {data:110}},
			         {sheet: 1, row: 13, col: 10, json: {data:600}},
			         
			         {sheet: 1, row: 16, col: 2, json: {data: "Blue fill if value equal 150", ff: 'Courier New'}},
			         {sheet: 1, row: 17, col: 2, json: {data:100}},
			         {sheet: 1, row: 17, col: 3, json: {data:200}},
			         {sheet: 1, row: 17, col: 4, json: {data:300}},
			         {sheet: 1, row: 17, col: 5, json: {data:400}},
			         {sheet: 1, row: 18, col: 2, json: {data:120}},
			         {sheet: 1, row: 18, col: 3, json: {data:220}},
			         {sheet: 1, row: 18, col: 4, json: {data:150}},
			         {sheet: 1, row: 18, col: 5, json: {data:500}},
			         {sheet: 1, row: 19, col: 2, json: {data:130}},
			         {sheet: 1, row: 19, col: 3, json: {data:170}},
			         {sheet: 1, row: 19, col: 4, json: {data:110}},
			         {sheet: 1, row: 19, col: 5, json: {data:600}},
			         
			         {sheet: 1, row: 16, col: 7, json: {data: "Orange text if values are repeat", ff: 'Courier New'}},
			         {sheet: 1, row: 17, col: 7, json: {data:100}},
			         {sheet: 1, row: 17, col: 8, json: {data:200}},
			         {sheet: 1, row: 17, col: 9, json: {data:300}},
			         {sheet: 1, row: 17, col: 10, json: {data:400}},
			         {sheet: 1, row: 18, col: 7, json: {data:200}},
			         {sheet: 1, row: 18, col: 8, json: {data:220}},
			         {sheet: 1, row: 18, col: 9, json: {data:150}},
			         {sheet: 1, row: 18, col: 10, json: {data:500}},
			         {sheet: 1, row: 19, col: 7, json: {data:130}},
			         {sheet: 1, row: 19, col: 8, json: {data:170}},
			         {sheet: 1, row: 19, col: 9, json: {data:110}},
			         {sheet: 1, row: 19, col: 10, json: {data:200}},
			    ]
			},
			
			/**
			 * This is the example Json Data for highlighting cell based on condition rule
			 * See running example @
			 *     EnterpriseSheet Samples / APIs -> Conditional format -> Text condition rule
			 */
			featureConditionStringJson : {
				fileName: 'Text condition rule',
		        sheets: [
	                {name: 'String condition', id: 1, color: 'orange' }
	            ],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,10]" },
	                { sheet:1, name:"merge2", ftype:"meg", json:"[4,2,4,5]" },
	                { sheet:1, name:"merge3", ftype:"meg", json:"[4,7,4,10]" },
	                { sheet:1, name:"merge4", ftype:"meg", json:"[10,2,10,5]" },
	                { sheet:1, name:"merge5", ftype:"meg", json:"[10,7,10,10]" },
	                { sheet:1, name:"condition1", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,5,2,7,5],type:1}],opt:{type:\"include\",base:\"TEST\",style:{cbgc:\"rgb(248,105,107)\",ccolor:\"white\"}},id:\"condition1\"}" },
	                { sheet:1, name:"condition2", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,5,7,7,10],type:1}],opt:{type:\"include\",base:\"TEST\",style:{ccolor:\"red\"}},id:\"condition2\"}" },
	                { sheet:1, name:"condition3", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,11,2,13,5],type:1}],opt:{type:\"repeat\",base:0,style:{cbgc:\"orange\",ccolor:\"white\"}},id:\"condition3\"}" },
	                { sheet:1, name:"condition4", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,11,7,13,10],type:1}],opt:{type:\"repeat\",base:0,style:{ccolor:\"blue\"}},id:\"condition4\"}" }   
	            ],
			    cells: [
			         {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
			         {sheet: 1, row: 2, col: 2, json: {data: "Add string conditional format to the cell range", fw:"bold", fz:14 }}, 
			         
			         {sheet: 1, row: 4, col: 2, json: {data: "Red fill if value contains TEST", ff: 'Courier New'}},
			         {sheet: 1, row: 5, col: 2, json: {data: "TEST"}},
			         {sheet: 1, row: 5, col: 3, json: {data: "OK"}},
			         {sheet: 1, row: 5, col: 4, json: {data: "Tuesday"}},
			         {sheet: 1, row: 5, col: 5, json: {data:400}},
			         {sheet: 1, row: 6, col: 2, json: {data: "test ok" }},
			         {sheet: 1, row: 6, col: 3, json: {data: "Repeat"}},
			         {sheet: 1, row: 6, col: 4, json: {data: "OK"}},
			         {sheet: 1, row: 6, col: 5, json: {data:500}},
			         {sheet: 1, row: 7, col: 2, json: {data:130}},
			         {sheet: 1, row: 7, col: 3, json: {data: "U too"}},
			         {sheet: 1, row: 7, col: 4, json: {data: "This is TEST"}},
			         {sheet: 1, row: 7, col: 5, json: {data: "Repeat"}},
			         
			         {sheet: 1, row: 4, col: 7, json: {data: "Red text if value contains TEST", ff: 'Courier New'}},
			         {sheet: 1, row: 5, col: 7, json: {data: "TEST"}},
			         {sheet: 1, row: 5, col: 8, json: {data: "OK"}},
			         {sheet: 1, row: 5, col: 9, json: {data: "Tuesday"}},
			         {sheet: 1, row: 5, col: 10, json: {data:400}},
			         {sheet: 1, row: 6, col: 7, json: {data: "test ok" }},
			         {sheet: 1, row: 6, col: 8, json: {data: "Repeat"}},
			         {sheet: 1, row: 6, col: 9, json: {data: "OK"}},
			         {sheet: 1, row: 6, col: 10, json: {data:500}},
			         {sheet: 1, row: 7, col: 7, json: {data:130}},
			         {sheet: 1, row: 7, col: 8, json: {data: "U too"}},
			         {sheet: 1, row: 7, col: 9, json: {data: "This is TEST"}},
			         {sheet: 1, row: 7, col: 10, json: {data: "Repeat"}},
			         
			         {sheet: 1, row: 10, col: 2, json: {data: "Orange fill if values are repeat", ff: 'Courier New'}},
			         {sheet: 1, row: 11, col: 2, json: {data: "This is OK"}},
			         {sheet: 1, row: 11, col: 3, json: {data:200}},
			         {sheet: 1, row: 11, col: 4, json: {data:"OK"}},
			         {sheet: 1, row: 11, col: 5, json: {data: "This is OK"}},
			         {sheet: 1, row: 12, col: 2, json: {data: "U too"}},
			         {sheet: 1, row: 12, col: 3, json: {data:220}},
			         {sheet: 1, row: 12, col: 4, json: {data: "Repeat"}},
			         {sheet: 1, row: 12, col: 5, json: {data:500}},
			         {sheet: 1, row: 13, col: 2, json: {data:"OK"}},
			         {sheet: 1, row: 13, col: 3, json: {data: "This is OK"}},
			         {sheet: 1, row: 13, col: 4, json: {data:110}},
			         {sheet: 1, row: 13, col: 5, json: {data: "Repeat"}},
			         
			         {sheet: 1, row: 10, col: 7, json: {data: "Blue text if values are repeat", ff: 'Courier New'}},
			         {sheet: 1, row: 11, col: 7, json: {data: "This is OK"}},
			         {sheet: 1, row: 11, col: 8, json: {data:200}},
			         {sheet: 1, row: 11, col: 9, json: {data:300}},
			         {sheet: 1, row: 11, col: 10, json: {data: "This is OK"}},
			         {sheet: 1, row: 12, col: 7, json: {data:120}},
			         {sheet: 1, row: 12, col: 8, json: {data: "U too"}},
			         {sheet: 1, row: 12, col: 9, json: {data:150}},
			         {sheet: 1, row: 12, col: 10, json: {data: "This is OK"}},
			         {sheet: 1, row: 13, col: 7, json: {data:130}},
			         {sheet: 1, row: 13, col: 8, json: {data: "U too 2"}},
			         {sheet: 1, row: 13, col: 9, json: {data:110}},
			         {sheet: 1, row: 13, col: 10, json: {data: "Repeat"}}
			    ]
			},
			
			/**
			 * This is the example Json Data for highlighting cell based on condition rule
			 * See running example @
			 *     EnterpriseSheet Samples / APIs -> Conditional format -> Date occruing rule
			 */
			featureConditionDateJson : {
				fileName: 'Date condition',
		        sheets: [
	                {name: 'Date occurring', id: 1, color: 'purple' }
	            ],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,10]" },
	                { sheet:1, name:"merge2", ftype:"meg", json:"[4,2,4,5]" },
	                { sheet:1, name:"merge3", ftype:"meg", json:"[4,7,4,10]" },
	                { sheet:1, name:"merge4", ftype:"meg", json:"[10,2,10,5]" },
	                { sheet:1, name:"merge5", ftype:"meg", json:"[10,7,10,10]" },
	                { sheet:1, name:"merge6", ftype:"meg", json:"[16,2,16,5]" },
	                { sheet:1, name:"merge7", ftype:"meg", json:"[16,7,16,10]" },
	                { sheet:1, name:"merge8", ftype:"meg", json:"[22,2,22,5]" },
	                { sheet:1, name:"merge9", ftype:"meg", json:"[22,7,22,10]" },
	                { sheet:1, name:"merge10", ftype:"meg", json:"[28,2,28,5]" },
	                { sheet:1, name:"merge11", ftype:"meg", json:"[28,7,28,10]" },
	                { sheet:1, name:"condition1", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,5,2,7,5],type:1}],opt:{type:\"date\",base:0,style:{cbgc:\"rgb(248,105,107)\",ccolor:\"white\"}},id:\"condition1\"}" },
	                { sheet:1, name:"condition2", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,5,7,7,10],type:1}],opt:{type:\"date\",base:1,style:{ccolor:\"red\"}},id:\"condition2\"}" },
	                { sheet:1, name:"condition3", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,11,2,13,5],type:1}],opt:{type:\"date\",base:2,style:{cbgc:\"orange\",ccolor:\"white\"}},id:\"condition3\"}" },
	                { sheet:1, name:"condition4", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,11,7,13,10],type:1}],opt:{type:\"date\",base:3,style:{ccolor:\"blue\"}},id:\"condition4\"}" },  
	                { sheet:1, name:"condition5", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,17,2,19,5],type:1}],opt:{type:\"date\",base:4,style:{cbgc:\"blue\",ccolor:\"white\"}},id:\"condition5\"}" },
	                { sheet:1, name:"condition6", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,17,7,19,10],type:1}],opt:{type:\"date\",base:5,style:{ccolor:\"orange\"}},id:\"condition6\"}" },
	                { sheet:1, name:"condition7", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,23,2,25,5],type:1}],opt:{type:\"date\",base:6,style:{cbgc:\"orange\",ccolor:\"white\"}},id:\"condition7\"}" },
	                { sheet:1, name:"condition8", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,23,7,25,10],type:1}],opt:{type:\"date\",base:7,style:{ccolor:\"blue\"}},id:\"condition8\"}" },                  
	                { sheet:1, name:"condition9", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,29,2,31,5],type:1}],opt:{type:\"date\",base:8,style:{cbgc:\"orange\",ccolor:\"white\"}},id:\"condition9\"}" },
	                { sheet:1, name:"condition10", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,29,7,31,10],type:1}],opt:{type:\"date\",base:9,style:{ccolor:\"blue\"}},id:\"condition10\"}" },                  
	            ],
			    cells: [
			         {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
			         {sheet: 1, row: 2, col: 2, json: {data: "Add date conditional format to the cell range", fw:"bold", fz:14 }}, 
			         
			         {sheet: 1, row: 4, col: 2, json: {data: "Red fill if date is yesterday", ff: 'Courier New'}},
			         {sheet: 1, row: 5, col: 2, json: {data:"=today()-1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 5, col: 3, json: {data:"=today()+1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 5, col: 4, json: {data:"=today()-2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 5, col: 5, json: {data:"=today()+2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 6, col: 2, json: {data:"=today()-7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 6, col: 3, json: {data:"=today()+7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 6, col: 4, json: {data:"=today()-30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 6, col: 5, json: {data:"=today()+30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 7, col: 2, json: {data:"=today()+3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 7, col: 3, json: {data:"=today()-3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 7, col: 4, json: {data:"=today()-50", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 7, col: 5, json: {data:"=today()", cal: true, fm: "date", "dfm":"Y m d"}},
			         
			         {sheet: 1, row: 4, col: 7, json: {data: "Red text if date is today", ff: 'Courier New'}},
			         {sheet: 1, row: 5, col: 7, json: {data:"=today()-1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 5, col: 8, json: {data:"=today()+1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 5, col: 9, json: {data:"=today()-2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 5, col: 10, json: {data:"=today()+2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 6, col: 7, json: {data:"=today()-7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 6, col: 8, json: {data:"=today()+7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 6, col: 9, json: {data:"=today()-30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 6, col: 10, json: {data:"=today()+30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 7, col: 7, json: {data:"=today()+3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 7, col: 8, json: {data:"=today()-3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 7, col: 9, json: {data:"=today()-50", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 7, col: 10, json: {data:"=today()", cal: true, fm: "date", "dfm":"Y m d"}},

			         {sheet: 1, row: 10, col: 2, json: {data: "Orange fill if date is tomorrow", ff: 'Courier New'}},
			         {sheet: 1, row: 11, col: 2, json: {data:"=today()-1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 11, col: 3, json: {data:"=today()+1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 11, col: 4, json: {data:"=today()-2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 11, col: 5, json: {data:"=today()+2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 12, col: 2, json: {data:"=today()-7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 12, col: 3, json: {data:"=today()+7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 12, col: 4, json: {data:"=today()-30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 12, col: 5, json: {data:"=today()+30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 13, col: 2, json: {data:"=today()+3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 13, col: 3, json: {data:"=today()-3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 13, col: 4, json: {data:"=today()-50", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 13, col: 5, json: {data:"=today()", cal: true, fm: "date", "dfm":"Y m d"}},
			         
			         {sheet: 1, row: 10, col: 7, json: {data: "Blue text if date is last 7 days", ff: 'Courier New'}},
			         {sheet: 1, row: 11, col: 7, json: {data:"=today()-1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 11, col: 8, json: {data:"=today()+1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 11, col: 9, json: {data:"=today()-2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 11, col: 10, json: {data:"=today()+2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 12, col: 7, json: {data:"=today()-7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 12, col: 8, json: {data:"=today()+7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 12, col: 9, json: {data:"=today()-30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 12, col: 10, json: {data:"=today()+30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 13, col: 7, json: {data:"=today()+3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 13, col: 8, json: {data:"=today()-3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 13, col: 9, json: {data:"=today()-50", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 13, col: 10, json: {data:"=today()", cal: true, fm: "date", "dfm":"Y m d"}},
			         
			         {sheet: 1, row: 16, col: 2, json: {data: "Orange fill if date is last week", ff: 'Courier New'}},
			         {sheet: 1, row: 17, col: 2, json: {data:"=today()-1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 17, col: 3, json: {data:"=today()+1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 17, col: 4, json: {data:"=today()-2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 17, col: 5, json: {data:"=today()+2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 18, col: 2, json: {data:"=today()-7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 18, col: 3, json: {data:"=today()+7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 18, col: 4, json: {data:"=today()-30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 18, col: 5, json: {data:"=today()+30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 19, col: 2, json: {data:"=today()+3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 19, col: 3, json: {data:"=today()-3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 19, col: 4, json: {data:"=today()-50", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 19, col: 5, json: {data:"=today()", cal: true, fm: "date", "dfm":"Y m d"}},
			         
			         {sheet: 1, row: 16, col: 7, json: {data: "Orange text if date is this week", ff: 'Courier New'}},
			         {sheet: 1, row: 17, col: 7, json: {data:"=today()-1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 17, col: 8, json: {data:"=today()+1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 17, col: 9, json: {data:"=today()-2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 17, col: 10, json: {data:"=today()+2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 18, col: 7, json: {data:"=today()-7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 18, col: 8, json: {data:"=today()+7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 18, col: 9, json: {data:"=today()-30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 18, col: 10, json: {data:"=today()+30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 19, col: 7, json: {data:"=today()+3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 19, col: 8, json: {data:"=today()-3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 19, col: 9, json: {data:"=today()-50", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 19, col: 10, json: {data:"=today()", cal: true, fm: "date", "dfm":"Y m d"}},
			         
			         {sheet: 1, row: 22, col: 2, json: {data: "Orange fill if date is next week", ff: 'Courier New'}},
			         {sheet: 1, row: 23, col: 2, json: {data:"=today()-1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 23, col: 3, json: {data:"=today()+1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 23, col: 4, json: {data:"=today()-2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 23, col: 5, json: {data:"=today()+2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 24, col: 2, json: {data:"=today()-7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 24, col: 3, json: {data:"=today()+7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 24, col: 4, json: {data:"=today()-30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 24, col: 5, json: {data:"=today()+30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 25, col: 2, json: {data:"=today()+3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 25, col: 3, json: {data:"=today()-3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 25, col: 4, json: {data:"=today()-50", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 25, col: 5, json: {data:"=today()", cal: true, fm: "date", "dfm":"Y m d"}},
			         
			         {sheet: 1, row: 22, col: 7, json: {data: "Blue text if date is last month", ff: 'Courier New'}},
			         {sheet: 1, row: 23, col: 7, json: {data:"=today()-1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 23, col: 8, json: {data:"=today()+1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 23, col: 9, json: {data:"=today()-2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 23, col: 10, json: {data:"=today()+2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 24, col: 7, json: {data:"=today()-7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 24, col: 8, json: {data:"=today()+7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 24, col: 9, json: {data:"=today()-30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 24, col: 10, json: {data:"=today()+30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 25, col: 7, json: {data:"=today()+3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 25, col: 8, json: {data:"=today()-3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 25, col: 9, json: {data:"=today()-50", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 25, col: 10, json: {data:"=today()", cal: true, fm: "date", "dfm":"Y m d"}},
			         
			         {sheet: 1, row: 28, col: 2, json: {data: "Orange fill if date is this month", ff: 'Courier New'}},
			         {sheet: 1, row: 29, col: 2, json: {data:"=today()-1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 29, col: 3, json: {data:"=today()+1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 29, col: 4, json: {data:"=today()-2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 29, col: 5, json: {data:"=today()+2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 30, col: 2, json: {data:"=today()-7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 30, col: 3, json: {data:"=today()+7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 30, col: 4, json: {data:"=today()-30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 30, col: 5, json: {data:"=today()+30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 31, col: 2, json: {data:"=today()+3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 31, col: 3, json: {data:"=today()-3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 31, col: 4, json: {data:"=today()-50", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 31, col: 5, json: {data:"=today()", cal: true, fm: "date", "dfm":"Y m d"}},
			         
			         {sheet: 1, row: 28, col: 7, json: {data: "Blue text if date is next month", ff: 'Courier New'}},
			         {sheet: 1, row: 29, col: 7, json: {data:"=today()-1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 29, col: 8, json: {data:"=today()+1", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 29, col: 9, json: {data:"=today()-2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 29, col: 10, json: {data:"=today()+2", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 30, col: 7, json: {data:"=today()-7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 30, col: 8, json: {data:"=today()+7", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 30, col: 9, json: {data:"=today()-30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 30, col: 10, json: {data:"=today()+30", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 31, col: 7, json: {data:"=today()+3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 31, col: 8, json: {data:"=today()-3", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 31, col: 9, json: {data:"=today()-50", cal: true, fm: "date", "dfm":"Y m d"}},
			         {sheet: 1, row: 31, col: 10, json: {data:"=today()", cal: true, fm: "date", "dfm":"Y m d"}},
			    ]
			},
			
			/**
			 * This is the example Json Data for highlighting cell based on condition rule
			 * See running example @
			 *     EnterpriseSheet Samples / APIs -> Conditional format -> Top/bottom rule
			 */
			featureConditionTopBottomJson : {
				fileName: 'Top/bottom rule',
		        sheets: [
	                {name: 'top/bottom', id: 1, color: 'orange' }
	            ],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,10]" },
	                { sheet:1, name:"merge2", ftype:"meg", json:"[4,2,4,5]" },
	                { sheet:1, name:"merge3", ftype:"meg", json:"[4,7,4,10]" },
	                { sheet:1, name:"merge4", ftype:"meg", json:"[10,2,10,5]" },
	                { sheet:1, name:"merge5", ftype:"meg", json:"[10,7,10,10]" },
	                { sheet:1, name:"merge6", ftype:"meg", json:"[16,2,16,5]" },
	                { sheet:1, name:"merge7", ftype:"meg", json:"[16,7,16,10]" },
	                { sheet:1, name:"condition1", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,5,2,7,5],type:1}],opt:{type:\"max\",base:5,style:{cbgc:\"rgb(248,105,107)\",ccolor:\"white\"}},id:\"condition1\"}" },
	                { sheet:1, name:"condition2", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,5,7,7,10],type:1}],opt:{type:\"top\",base:20,style:{ccolor:\"red\"}},id:\"condition2\"}" },
	                { sheet:1, name:"condition3", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,11,2,13,5],type:1}],opt:{type:\"min\",base:5,style:{cbgc:\"orange\",ccolor:\"white\"}},id:\"condition3\"}" },
	                { sheet:1, name:"condition4", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,11,7,13,10],type:1}],opt:{type:\"bottom\",base:20,style:{ccolor:\"blue\"}},id:\"condition4\"}" },
	                { sheet:1, name:"condition5", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,17,2,19,5],type:1}],opt:{type:\"average\",base:0,style:{cbgc:\"blue\",ccolor:\"white\"}},id:\"condition5\"}" },
	                { sheet:1, name:"condition6", ftype:"cdt", json: "{name:\"boolstyle\",rng:[{span:[1,17,7,19,10],type:1}],opt:{type:\"average\",base:1,style:{ccolor:\"orange\"}},id:\"condition6\"}" }                
	            ],
			    cells: [
			         {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
			         {sheet: 1, row: 2, col: 2, json: {data: "Find the highest/lowest values", fw:"bold", fz:14 }}, 
			         
			         {sheet: 1, row: 4, col: 2, json: {data: "Red fill if it is top 5 value", ff: 'Courier New'}},
			         {sheet: 1, row: 5, col: 2, json: {data:100}},
			         {sheet: 1, row: 5, col: 3, json: {data:200}},
			         {sheet: 1, row: 5, col: 4, json: {data:300}},
			         {sheet: 1, row: 5, col: 5, json: {data:400}},
			         {sheet: 1, row: 6, col: 2, json: {data:120}},
			         {sheet: 1, row: 6, col: 3, json: {data:220}},
			         {sheet: 1, row: 6, col: 4, json: {data:150}},
			         {sheet: 1, row: 6, col: 5, json: {data:500}},
			         {sheet: 1, row: 7, col: 2, json: {data:130}},
			         {sheet: 1, row: 7, col: 3, json: {data:170}},
			         {sheet: 1, row: 7, col: 4, json: {data:110}},
			         {sheet: 1, row: 7, col: 5, json: {data:600}},
			         
			         {sheet: 1, row: 4, col: 7, json: {data: "Red text if it is top 20% value", ff: 'Courier New'}},
			         {sheet: 1, row: 5, col: 7, json: {data:100}},
			         {sheet: 1, row: 5, col: 8, json: {data:200}},
			         {sheet: 1, row: 5, col: 9, json: {data:300}},
			         {sheet: 1, row: 5, col: 10, json: {data:400}},
			         {sheet: 1, row: 6, col: 7, json: {data:120}},
			         {sheet: 1, row: 6, col: 8, json: {data:220}},
			         {sheet: 1, row: 6, col: 9, json: {data:150}},
			         {sheet: 1, row: 6, col: 10, json: {data:500}},
			         {sheet: 1, row: 7, col: 7, json: {data:130}},
			         {sheet: 1, row: 7, col: 8, json: {data:170}},
			         {sheet: 1, row: 7, col: 9, json: {data:110}},
			         {sheet: 1, row: 7, col: 10, json: {data:600}},
			         
			         {sheet: 1, row: 10, col: 2, json: {data: "Orange fill if it is top 5 value", ff: 'Courier New'}},
			         {sheet: 1, row: 11, col: 2, json: {data:100}},
			         {sheet: 1, row: 11, col: 3, json: {data:200}},
			         {sheet: 1, row: 11, col: 4, json: {data:300}},
			         {sheet: 1, row: 11, col: 5, json: {data:400}},
			         {sheet: 1, row: 12, col: 2, json: {data:120}},
			         {sheet: 1, row: 12, col: 3, json: {data:220}},
			         {sheet: 1, row: 12, col: 4, json: {data:150}},
			         {sheet: 1, row: 12, col: 5, json: {data:500}},
			         {sheet: 1, row: 13, col: 2, json: {data:130}},
			         {sheet: 1, row: 13, col: 3, json: {data:170}},
			         {sheet: 1, row: 13, col: 4, json: {data:110}},
			         {sheet: 1, row: 13, col: 5, json: {data:600}},
			         
			         {sheet: 1, row: 10, col: 7, json: {data: "Blue text if it is bottom 20% value", ff: 'Courier New'}},
			         {sheet: 1, row: 11, col: 7, json: {data:100}},
			         {sheet: 1, row: 11, col: 8, json: {data:200}},
			         {sheet: 1, row: 11, col: 9, json: {data:300}},
			         {sheet: 1, row: 11, col: 10, json: {data:400}},
			         {sheet: 1, row: 12, col: 7, json: {data:120}},
			         {sheet: 1, row: 12, col: 8, json: {data:220}},
			         {sheet: 1, row: 12, col: 9, json: {data:150}},
			         {sheet: 1, row: 12, col: 10, json: {data:500}},
			         {sheet: 1, row: 13, col: 7, json: {data:130}},
			         {sheet: 1, row: 13, col: 8, json: {data:170}},
			         {sheet: 1, row: 13, col: 9, json: {data:110}},
			         {sheet: 1, row: 13, col: 10, json: {data:600}},
			         
			         {sheet: 1, row: 16, col: 2, json: {data: "Blue fill if values above average", ff: 'Courier New'}},
			         {sheet: 1, row: 17, col: 2, json: {data:100}},
			         {sheet: 1, row: 17, col: 3, json: {data:200}},
			         {sheet: 1, row: 17, col: 4, json: {data:300}},
			         {sheet: 1, row: 17, col: 5, json: {data:400}},
			         {sheet: 1, row: 18, col: 2, json: {data:120}},
			         {sheet: 1, row: 18, col: 3, json: {data:220}},
			         {sheet: 1, row: 18, col: 4, json: {data:150}},
			         {sheet: 1, row: 18, col: 5, json: {data:500}},
			         {sheet: 1, row: 19, col: 2, json: {data:130}},
			         {sheet: 1, row: 19, col: 3, json: {data:170}},
			         {sheet: 1, row: 19, col: 4, json: {data:110}},
			         {sheet: 1, row: 19, col: 5, json: {data:600}},
			         
			         {sheet: 1, row: 16, col: 7, json: {data: "Orange text if values below average", ff: 'Courier New'}},
			         {sheet: 1, row: 17, col: 7, json: {data:100}},
			         {sheet: 1, row: 17, col: 8, json: {data:200}},
			         {sheet: 1, row: 17, col: 9, json: {data:300}},
			         {sheet: 1, row: 17, col: 10, json: {data:400}},
			         {sheet: 1, row: 18, col: 7, json: {data:200}},
			         {sheet: 1, row: 18, col: 8, json: {data:220}},
			         {sheet: 1, row: 18, col: 9, json: {data:150}},
			         {sheet: 1, row: 18, col: 10, json: {data:500}},
			         {sheet: 1, row: 19, col: 7, json: {data:130}},
			         {sheet: 1, row: 19, col: 8, json: {data:170}},
			         {sheet: 1, row: 19, col: 9, json: {data:110}},
			         {sheet: 1, row: 19, col: 10, json: {data:200}},
			    ]
			},
			
			/**
			 * This is the example Json Data for data bar
			 * See running example @
			 *     EnterpriseSheet Samples / APIs -> Conditional format -> Data bar
			 */
			featureConditionBarJson : {
				fileName: 'Data bar condition',
		        sheets: [
	                {name: 'data bar', id: 1, color: 'purple' }
	            ],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,10]" },
	                { sheet:1, name:"condition1", ftype:"cdt", json: "{name:\"colorbar\",rng:[{span:[1,4,2,13,2],type:1}],opt:{pos:\"rgb(248,105,107)\",neg:\"rgb(150,0,0)\"},id:\"condition1\"}" },
	                { sheet:1, name:"condition2", ftype:"cdt", json: "{name:\"colorbar\",rng:[{span:[1,4,4,13,4],type:1}],opt:{pos:\"red\",neg:\"orange\"},id:\"condition2\"}" },
	                { sheet:1, name:"condition3", ftype:"cdt", json: "{name:\"colorbar\",rng:[{span:[1,4,6,13,6],type:1}],opt:{pos:\"blue\",neg:\"gray\"},id:\"condition3\"}" }
	            ],
			    cells: [
			         {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
			         {sheet: 1, row: 2, col: 2, json: {data: "Add data bar to the cell range", fw:"bold", fz:14 }}, 

			         {sheet: 1, row: 4, col: 2, json: {data: 1}},
			         {sheet: 1, row: 5, col: 2, json: {data: 2}},
			         {sheet: 1, row: 6, col: 2, json: {data: 3}},
			         {sheet: 1, row: 7, col: 2, json: {data: 4}},
			         {sheet: 1, row: 8, col: 2, json: {data: 5}},
			         {sheet: 1, row: 9, col: 2, json: {data: 6}},
			         {sheet: 1, row: 10, col: 2, json: {data: 7}},
			         {sheet: 1, row: 11, col: 2, json: {data: 8}},
			         {sheet: 1, row: 12, col: 2, json: {data: 9}},
			         {sheet: 1, row: 13, col: 2, json: {data: 10}},
			         
			         {sheet: 1, row: 4, col: 4, json: {data: -5}},
			         {sheet: 1, row: 5, col: 4, json: {data: -4}},
			         {sheet: 1, row: 6, col: 4, json: {data: -3}},
			         {sheet: 1, row: 7, col: 4, json: {data: -2}},
			         {sheet: 1, row: 8, col: 4, json: {data: -1}},
			         {sheet: 1, row: 9, col: 4, json: {data: 0}},
			         {sheet: 1, row: 10, col: 4, json: {data: 1}},
			         {sheet: 1, row: 11, col: 4, json: {data: 2}},
			         {sheet: 1, row: 12, col: 4, json: {data: 3}},
			         {sheet: 1, row: 13, col: 4, json: {data: 4}},
			         
			         {sheet: 1, row: 4, col: 6, json: {data: -5}},
			         {sheet: 1, row: 5, col: 6, json: {data: -4}},
			         {sheet: 1, row: 6, col: 6, json: {data: -3}},
			         {sheet: 1, row: 7, col: 6, json: {data: -2}},
			         {sheet: 1, row: 8, col: 6, json: {data: -1}},
			         {sheet: 1, row: 9, col: 6, json: {data: 0}},
			         {sheet: 1, row: 10, col: 6, json: {data: 1}},
			         {sheet: 1, row: 11, col: 6, json: {data: 2}},
			         {sheet: 1, row: 12, col: 6, json: {data: 3}},
			         {sheet: 1, row: 13, col: 6, json: {data: 4}},
			    ]
			},
			
			/**
			 * This is the example Json Data for color scales
			 * See running example @
			 *     EnterpriseSheet Samples / APIs -> Conditional format -> Color scales
			 */
			featureConditionColorScalesJson : {
				fileName: 'Color scales',
		        sheets: [
	                {name: 'color scale', id: 1, color: 'purple' }
	            ],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,10]" },
	                { sheet:1, name:"condition1", ftype:"cdt", json: "{name:\"colorgrad\",rng:[{span:[1,4,2,13,2],type:1}],opt:{start:[90,190,213],end:[248,105,107],stop:[255,235,132]},id:\"condition1\"}" },
	                { sheet:1, name:"condition2", ftype:"cdt", json: "{name:\"colorgrad\",rng:[{span:[1,4,4,13,4],type:1}],opt:{start:[248,105,107],end:[90,190,213],stop:[255,235,132]},id:\"condition2\"}" },
	                { sheet:1, name:"condition3", ftype:"cdt", json: "{name:\"colorgrad\",rng:[{span:[1,4,6,13,6],type:1}],opt:{start:[90,190,213],end:[255,235,132]},id:\"condition3\"}" },
	                { sheet:1, name:"condition4", ftype:"cdt", json: "{name:\"colorgrad\",rng:[{span:[1,4,8,13,8],type:1}],opt:{start:[248,105,107],end:[255,235,132]},id:\"condition4\"}" },
	                { sheet:1, name:"condition5", ftype:"cdt", json: "{name:\"colorgrad\",rng:[{span:[1,4,10,13,10],type:1}],opt:{start:[255,255,255],end:[248,105,107]},id:\"condition5\"}" },
	                { sheet:1, name:"condition6", ftype:"cdt", json: "{name:\"colorgrad\",rng:[{span:[1,4,12,13,12],type:1}],opt:{start:[90,190,123],end:[255,255,255]},id:\"condition6\"}" },
	            ],
			    cells: [
			         {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
			         {sheet: 1, row: 2, col: 2, json: {data: "Apply color scales to the cell range", fw:"bold", fz:14 }}, 

			         {sheet: 1, row: 4, col: 2, json: {data: 1}},
			         {sheet: 1, row: 5, col: 2, json: {data: 2}},
			         {sheet: 1, row: 6, col: 2, json: {data: 3}},
			         {sheet: 1, row: 7, col: 2, json: {data: 4}},
			         {sheet: 1, row: 8, col: 2, json: {data: 5}},
			         {sheet: 1, row: 9, col: 2, json: {data: 6}},
			         {sheet: 1, row: 10, col: 2, json: {data: 7}},
			         {sheet: 1, row: 11, col: 2, json: {data: 8}},
			         {sheet: 1, row: 12, col: 2, json: {data: 9}},
			         {sheet: 1, row: 13, col: 2, json: {data: 10}},
			         
			         {sheet: 1, row: 4, col: 4, json: {data: 1}},
			         {sheet: 1, row: 5, col: 4, json: {data: 2}},
			         {sheet: 1, row: 6, col: 4, json: {data: 3}},
			         {sheet: 1, row: 7, col: 4, json: {data: 4}},
			         {sheet: 1, row: 8, col: 4, json: {data: 5}},
			         {sheet: 1, row: 9, col: 4, json: {data: 6}},
			         {sheet: 1, row: 10, col: 4, json: {data: 7}},
			         {sheet: 1, row: 11, col: 4, json: {data: 8}},
			         {sheet: 1, row: 12, col: 4, json: {data: 9}},
			         {sheet: 1, row: 13, col: 4, json: {data: 10}},
			         
			         {sheet: 1, row: 4, col: 6, json: {data: 1}},
			         {sheet: 1, row: 5, col: 6, json: {data: 2}},
			         {sheet: 1, row: 6, col: 6, json: {data: 3}},
			         {sheet: 1, row: 7, col: 6, json: {data: 4}},
			         {sheet: 1, row: 8, col: 6, json: {data: 5}},
			         {sheet: 1, row: 9, col: 6, json: {data: 6}},
			         {sheet: 1, row: 10, col: 6, json: {data: 7}},
			         {sheet: 1, row: 11, col: 6, json: {data: 8}},
			         {sheet: 1, row: 12, col: 6, json: {data: 9}},
			         {sheet: 1, row: 13, col: 6, json: {data: 10}},
			         
			         {sheet: 1, row: 4, col: 8, json: {data: 1}},
			         {sheet: 1, row: 5, col: 8, json: {data: 2}},
			         {sheet: 1, row: 6, col: 8, json: {data: 3}},
			         {sheet: 1, row: 7, col: 8, json: {data: 4}},
			         {sheet: 1, row: 8, col: 8, json: {data: 5}},
			         {sheet: 1, row: 9, col: 8, json: {data: 6}},
			         {sheet: 1, row: 10, col: 8, json: {data: 7}},
			         {sheet: 1, row: 11, col: 8, json: {data: 8}},
			         {sheet: 1, row: 12, col: 8, json: {data: 9}},
			         {sheet: 1, row: 13, col: 8, json: {data: 10}},
			         
			         {sheet: 1, row: 4, col: 10, json: {data: 1}},
			         {sheet: 1, row: 5, col: 10, json: {data: 2}},
			         {sheet: 1, row: 6, col: 10, json: {data: 3}},
			         {sheet: 1, row: 7, col: 10, json: {data: 4}},
			         {sheet: 1, row: 8, col: 10, json: {data: 5}},
			         {sheet: 1, row: 9, col: 10, json: {data: 6}},
			         {sheet: 1, row: 10, col: 10, json: {data: 7}},
			         {sheet: 1, row: 11, col: 10, json: {data: 8}},
			         {sheet: 1, row: 12, col: 10, json: {data: 9}},
			         {sheet: 1, row: 13, col: 10, json: {data: 10}},
			         
			         {sheet: 1, row: 4, col: 12, json: {data: 1}},
			         {sheet: 1, row: 5, col: 12, json: {data: 2}},
			         {sheet: 1, row: 6, col: 12, json: {data: 3}},
			         {sheet: 1, row: 7, col: 12, json: {data: 4}},
			         {sheet: 1, row: 8, col: 12, json: {data: 5}},
			         {sheet: 1, row: 9, col: 12, json: {data: 6}},
			         {sheet: 1, row: 10, col: 12, json: {data: 7}},
			         {sheet: 1, row: 11, col: 12, json: {data: 8}},
			         {sheet: 1, row: 12, col: 12, json: {data: 9}},
			         {sheet: 1, row: 13, col: 12, json: {data: 10}},
			    ]
			},
			
			
			/**
			 * This is the example Json Data for color scales
			 * See running example @
			 *     EnterpriseSheet Samples / APIs -> Conditional format -> Color scales
			 */
			featureConditionIconsetJson : {
				fileName: 'Icon sets',
		        sheets: [
	                {name: 'icon sets', id: 1, color: 'orange' }
	            ],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,10]" },
	                { sheet:1, name:"condition1", ftype:"cdt", json: '{name:"iconset",rng:[{span:[1,4,2,6,2],type:1}],opt:{set:0,level:3},id:"condition1"}' },
	                { sheet:1, name:"condition2", ftype:"cdt", json: "{name:\"iconset\",rng:[{span:[1,4,4,6,4],type:1}],opt:{set:1,level:3},id:\"condition2\"}" },
	                { sheet:1, name:"condition3", ftype:"cdt", json: "{name:\"iconset\",rng:[{span:[1,4,6,6,6],type:1}],opt:{set:2,level:3},id:\"condition3\"}" },
	                { sheet:1, name:"condition4", ftype:"cdt", json: "{name:\"iconset\",rng:[{span:[1,4,8,7,8],type:1}],opt:{set:3,level:4},id:\"condition4\"}" },
	                { sheet:1, name:"condition5", ftype:"cdt", json: "{name:\"iconset\",rng:[{span:[1,4,10,7,10],type:1}],opt:{set:4,level:4},id:\"condition5\"}" },
	                { sheet:1, name:"condition6", ftype:"cdt", json: "{name:\"iconset\",rng:[{span:[1,4,12,6,12],type:1}],opt:{set:5,level:3},id:\"condition6\"}" },
	                { sheet:1, name:"condition7", ftype:"cdt", json: "{name:\"iconset\",rng:[{span:[1,9,2,11,2],type:1}],opt:{set:6,level:3},id:\"condition7\"}" },
	                { sheet:1, name:"condition8", ftype:"cdt", json: "{name:\"iconset\",rng:[{span:[1,9,4,11,4],type:1}],opt:{set:7,level:3},id:\"condition8\"}" },
	                { sheet:1, name:"condition9", ftype:"cdt", json: "{name:\"iconset\",rng:[{span:[1,9,6,11,6],type:1}],opt:{set:8,level:3},id:\"condition9\"}" },
	                { sheet:1, name:"condition10", ftype:"cdt", json: "{name:\"iconset\",rng:[{span:[1,9,8,11,8],type:1}],opt:{set:9,level:3},id:\"condition10\"}" },
	                { sheet:1, name:"condition11", ftype:"cdt", json: "{name:\"iconset\",rng:[{span:[1,9,10,11,10],type:1}],opt:{is:[{set:9,idx:2},{set:8,idx:2},{set:1,idx:0}],rv:false,only:false,ths:[{v:34,c:\"<=\",u:\"percent\"},{v:67,c:\"<=\",u:\"percent\"}]},id:\"condition11\"}" },
	                { sheet:1, name:"condition12", ftype:"cdt", json: "{name:\"iconset\",rng:[{span:[1,9,12,11,12],type:1}],opt:{is:[{set:0,idx:0},{set:0,idx:1},{set:0,idx:2}],rv:false,only:true,ths:[{v:5,c:\"<=\",u:\"number\"},{v:10,c:\"<=\",u:\"number\"}]},id:\"condition12\"}" }         
	            ],
			    cells: [
			         {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
			         {sheet: 1, row: 2, col: 2, json: {data: "Apply icon sets to the cell range", fw:"bold", fz:14 }}, 
			         
			         {sheet: 1, row: 4, col: 2, json: {data: 1}},
			         {sheet: 1, row: 5, col: 2, json: {data: 2}},
			         {sheet: 1, row: 6, col: 2, json: {data: 3}},		         
			         {sheet: 1, row: 4, col: 4, json: {data: 1}},
			         {sheet: 1, row: 5, col: 4, json: {data: 2}},
			         {sheet: 1, row: 6, col: 4, json: {data: 3}},			         
			         {sheet: 1, row: 4, col: 6, json: {data: 1}},
			         {sheet: 1, row: 5, col: 6, json: {data: 2}},
			         {sheet: 1, row: 6, col: 6, json: {data: 3}},			         
			         {sheet: 1, row: 4, col: 8, json: {data: 1}},
			         {sheet: 1, row: 5, col: 8, json: {data: 2}},
			         {sheet: 1, row: 6, col: 8, json: {data: 3}},
			         {sheet: 1, row: 7, col: 8, json: {data: 4}},
			         {sheet: 1, row: 4, col: 10, json: {data: 1}},
			         {sheet: 1, row: 5, col: 10, json: {data: 2}},
			         {sheet: 1, row: 6, col: 10, json: {data: 3}},	
			         {sheet: 1, row: 7, col: 10, json: {data: 4}},
			         {sheet: 1, row: 4, col: 12, json: {data: 1}},
			         {sheet: 1, row: 5, col: 12, json: {data: 2}},
			         {sheet: 1, row: 6, col: 12, json: {data: 3}},
			         
			         {sheet: 1, row: 9, col: 2, json: {data: 1}},
			         {sheet: 1, row: 10, col: 2, json: {data: 2}},
			         {sheet: 1, row: 11, col: 2, json: {data: 3}},		         
			         {sheet: 1, row: 9, col: 4, json: {data: 1}},
			         {sheet: 1, row: 10, col: 4, json: {data: 2}},
			         {sheet: 1, row: 11, col: 4, json: {data: 3}},			         
			         {sheet: 1, row: 9, col: 6, json: {data: 1}},
			         {sheet: 1, row: 10, col: 6, json: {data: 2}},
			         {sheet: 1, row: 11, col: 6, json: {data: 3}},			         
			         {sheet: 1, row: 9, col: 8, json: {data: 1}},
			         {sheet: 1, row: 10, col: 8, json: {data: 2}},
			         {sheet: 1, row: 11, col: 8, json: {data: 3}},		         
			         {sheet: 1, row: 9, col: 10, json: {data: 1}},
			         {sheet: 1, row: 10, col: 10, json: {data: 2}},
			         {sheet: 1, row: 11, col: 10, json: {data: 3}},			         
			         {sheet: 1, row: 9, col: 12, json: {data: 11}},
			         {sheet: 1, row: 10, col: 12, json: {data: 6}},
			         {sheet: 1, row: 11, col: 12, json: {data: 3}},
			    ]
			},
			
			/**
			 * This is the example Json Data for generate column/bar chart
			 * See running example @
			 *     EnterpriseSheet Samples / APIs -> Chart Features/APIs -> Generate column/bar chart
			 */
			featureChartBarJson :  {
				fileName: 'Column/Bar Chart',
		        sheets: [
	                {name: 'Column/bar', id: 1, color: 'orange' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" },
	               { sheet: 1, name: 'chart1', ftype: 'floor',
		              json: '{"seriesPosition":"row","legendPosition":"right","chartType":"column","floorType":"chart","source":{"series":[["",5,3,5,4],["",6,3,6,4],["",7,3,7,4]], "categories":[["",5,2,5,2],["",6,2,6,2],["",7,2,7,2]], "labels":[["",4,3,4,3],["",4,4,4,4]], "usAbs": true, "cacheFields":[{"name":"category"},{"name":"Monday","title":"Monday"},{"name":"Tuesday","title":"Tuesday"},{"name":"Wenseday","title":"Wenseday"}] },"x":550,"y":60,"width":400,"height":300, id: "chart1"}'},
	               { sheet: 1, name: 'chart2', ftype: 'floor',
		              json: '{"seriesPosition":"row","legendPosition":"right","chartType":"bar","floorType":"chart","source":{"series":[["",5,3,5,4],["",6,3,6,4],["",7,3,7,4]], "categories":[["",5,2,5,2],["",6,2,6,2],["",7,2,7,2]], "labels":[["",4,3,4,3],["",4,4,4,4]], "usAbs": true, "cacheFields":[{"name":"category"},{"name":"Monday","title":"Monday"},{"name":"Tuesday","title":"Tuesday"},{"name":"Wenseday","title":"Wenseday"}] },"x":100,"y":200,"width":400,"height":300, id: "chart2"}'},
	            ],
		        cells: [
		            {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 
		            {sheet: 1, row: 2, col: 2, json: {data: "Generate column/bar chart for cell ranges", fw:"bold", fz:14 }},
		            
				    {sheet: 1, row: 5, col: 2, json: { data: 'Monday', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}}, 
					{sheet: 1, row: 6, col: 2, json: { data: 'Tuesday', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}}, 
					{sheet: 1, row: 7, col: 2, json: { data: 'Wednesday', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}}, 
					{sheet: 1, row: 5, col: 3, json: { data: 2, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}}, 
					{sheet: 1, row: 6, col: 3, json: { data: -5, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 7, col: 3, json: { data: -16, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 5, col: 4, json: { data: 12, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 6, col: 4, json: { data: 5, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 7, col: 4, json: { data: -5, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 4, col: 2, json: { data: 'Weekday', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 4, col: 3, json: { data: '1st item', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 4, col: 4, json: { data: '2nd item', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}}
			    ]
			},
			
			/**
			 * This is the example Json Data for generate area chart
			 * See running example @
			 *     EnterpriseSheet Samples / APIs -> Chart Features/APIs -> Generate area chart
			 */
			featureChartAreaJson : {
				fileName: 'Area Chart',
		        sheets: [
	                {name: 'Area', id: 1, color: 'orange' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" },
	               { sheet: 1, name: 'chart12', ftype: 'floor',
		            json: '{"seriesPosition":"row","legendPosition":"right","chartType":"area","floorType":"chart","source":{"series":[["",5,3,5,4],["",6,3,6,4],["",7,3,7,4]], "categories":[["",5,2,5,2],["",6,2,6,2],["",7,2,7,2]], "labels":[["",4,3,4,3],["",4,4,4,4]], "usAbs": true, "cacheFields":[{"name":"category"},{"name":"Monday","title":"Monday"},{"name":"Tuesday","title":"Tuesday"},{"name":"Wenseday","title":"Wenseday"}] },"x":500,"y":60,"width":400,"height":300, id: "chart2"}'},
	            ],
		        cells: [
		            {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 
		            {sheet: 1, row: 2, col: 2, json: {data: "Generate area chart for cell ranges", fw:"bold", fz:14 }},
		            
				    {sheet: 1, row: 5, col: 2, json: { data: 'Monday', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}}, 
					{sheet: 1, row: 6, col: 2, json: { data: 'Tuesday', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}}, 
					{sheet: 1, row: 7, col: 2, json: { data: 'Wednesday', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}}, 
					{sheet: 1, row: 5, col: 3, json: { data: 2, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}}, 
					{sheet: 1, row: 6, col: 3, json: { data: -5, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 7, col: 3, json: { data: -16, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 5, col: 4, json: { data: 12, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 6, col: 4, json: { data: 5, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 7, col: 4, json: { data: -5, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 4, col: 2, json: { data: 'Weekday', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 4, col: 3, json: { data: '1st item', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 4, col: 4, json: { data: '2nd item', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}}
			    ]
			},
			
			/**
			 * This is the example Json Data for generate line chart
			 * See running example @
			 *     EnterpriseSheet Samples / APIs -> Chart Features/APIs -> Generate line chart
			 */
			featureChartLineJson : {
				fileName: 'Line / scatter Chart',
		        sheets: [
	                {name: 'Line', id: 1, color: 'orange' },
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" },
	               { sheet: 1, name: 'chart1', ftype: 'floor',
		              json: '{"seriesPosition":"row","legendPosition":"right","chartType":"line","floorType":"chart","source":{"series":[["",5,3,5,4],["",6,3,6,4],["",7,3,7,4]], "categories":[["",5,2,5,2],["",6,2,6,2],["",7,2,7,2]], "labels":[["",4,3,4,3],["",4,4,4,4]], "usAbs": true, "cacheFields":[{"name":"category"},{"name":"Monday","title":"Monday"},{"name":"Tuesday","title":"Tuesday"},{"name":"Wenseday","title":"Wenseday"}] },"x":550,"y":60,"width":400,"height":300, id: "chart1"}'},
	               { sheet: 1, name: 'chart2', ftype: 'floor',
		              json: '{"seriesPosition":"row","legendPosition":"right","chartType":"scatter","floorType":"chart","source":{"series":[["",5,3,5,4],["",6,3,6,4],["",7,3,7,4]], "categories":[["",5,2,5,2],["",6,2,6,2],["",7,2,7,2]], "labels":[["",4,3,4,3],["",4,4,4,4]], "usAbs": true, "cacheFields":[{"name":"category"},{"name":"Monday","title":"Monday"},{"name":"Tuesday","title":"Tuesday"},{"name":"Wenseday","title":"Wenseday"}] },"x":100,"y":200,"width":400,"height":300, id: "chart2"}'},
	            ],
		        cells: [
		            {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 
		            {sheet: 1, row: 2, col: 2, json: {data: "Generate line/scatter chart for cell ranges", fw:"bold", fz:14 }},
		            
				    {sheet: 1, row: 5, col: 2, json: { data: 'Monday', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}}, 
					{sheet: 1, row: 6, col: 2, json: { data: 'Tuesday', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}}, 
					{sheet: 1, row: 7, col: 2, json: { data: 'Wednesday', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}}, 
					{sheet: 1, row: 5, col: 3, json: { data: 2, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}}, 
					{sheet: 1, row: 6, col: 3, json: { data: -5, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 7, col: 3, json: { data: -16, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 5, col: 4, json: { data: 12, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 6, col: 4, json: { data: 5, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 7, col: 4, json: { data: -5, tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 4, col: 2, json: { data: 'Weekday', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 4, col: 3, json: { data: '1st item', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
					{sheet: 1, row: 4, col: 4, json: { data: '2nd item', tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}}
			    ]
			},
			
			/**
			 * This is the example Json Data for generate pie chart
			 * See running example @
			 *     EnterpriseSheet Samples / APIs -> Chart Features/APIs -> Generate pie chart
			 */
			featureChartPieJson : {
				fileName: 'Pie Chart',
		        sheets: [
	                {name: 'Pie', id: 1, color: 'orange' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" },
	               { sheet: 1, name: 'chart12', ftype: 'floor',
		             json: '{"seriesPosition":"col","legendPosition":"right","chartType":"pie","floorType":"chart","source":{"series":[["",5,3,10,3]], "labels":[["",5,2,5,2],["",6,2,6,2],["",7,2,7,2],["",8,2,8,2],["",9,2,9,2],["",10,2,10,2]], "usAbs": true },"x":400,"y":80,"width":400,"height":300, id: "chart12"}' }
	            ],
		        cells: [
		            {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 
		            {sheet: 1, row: 2, col: 2, json: {data: "Generate pie chart for cell ranges", fw:"bold", fz:14 }}, 
		            
		            {sheet: 1, row: 4, col: 2, json: { data: 'Brand', tpl: '{id: "tpl_27", span: [1,4,2,10,3]}'}}, 
		            {sheet: 1, row: 4, col: 3, json: { data: 'Total', tpl: '{id: "tpl_27", span: [1,4,2,10,3]}'}}, 
				    {sheet: 1, row: 5, col: 2, json: { data: 'Hybrid', tpl: '{id: "tpl_27", span: [1,4,2,10,3]}'}}, 
					{sheet: 1, row: 6, col: 2, json: { data: 'SUV', tpl: '{id: "tpl_27", span: [1,4,2,10,3]}'}}, 
					{sheet: 1, row: 7, col: 2, json: { data: 'Sedan', tpl: '{id: "tpl_27", span: [1,4,2,10,3]}'}}, 
					{sheet: 1, row: 8, col: 2, json: { data: 'Sports', tpl: '{id: "tpl_27", span: [1,4,2,10,3]}'}}, 
					{sheet: 1, row: 9, col: 2, json: { data: 'Truck', tpl: '{id: "tpl_27", span: [1,4,2,10,3]}'}}, 
					{sheet: 1, row: 10, col: 2, json: { data: 'Wagon', tpl: '{id: "tpl_27", span: [1,4,2,10,3]}'}}, 
					{sheet: 1, row: 5, col: 3, json: { data: 120, tpl: '{id: "tpl_27", span: [1,4,2,10,3]}'}},
					{sheet: 1, row: 6, col: 3, json: { data: 350, tpl: '{id: "tpl_27", span: [1,4,2,10,3]}'}},
					{sheet: 1, row: 7, col: 3, json: { data: 1050, tpl: '{id: "tpl_27", span: [1,4,2,10,3]}'}},
					{sheet: 1, row: 8, col: 3, json: { data: 200, tpl: '{id: "tpl_27", span: [1,4,2,10,3]}'}},
					{sheet: 1, row: 9, col: 3, json: { data: 500, tpl: '{id: "tpl_27", span: [1,4,2,10,3]}'}},
					{sheet: 1, row: 10, col: 3, json: { data: 600, tpl: '{id: "tpl_27", span: [1,4,2,10,3]}'}},
			    ]
			},
			
			/**
			 * This is the chart json example to show how to create sparkline by calling EnterpiseSheet API.
			 */
			featureSparklineJson : {
				fileName: 'Sparkline column chart',
		        sheets: [
	                {name: 'Column', id: 1, color: 'orange' }
	            ],
		        floatings: [
		           { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,10]" },
		           { sheet:1, name:"merge2", ftype:"meg", json:"[4,4,9,5]" },
		           { sheet:1, name:"merge3", ftype:"meg", json:"[4,9,9,10]" },
		           { sheet:1, name: 'sparklineChart1', ftype: 'cdt',
				    json: '{"name": "minichart", "rng":[{"span":[1,4,4,4,4],"type":1}], "opt":{"base":{"span":[1,4,2,9,2],"type":1},"type":"column","pc":"rgb(0,0,128)","nc":"rgb(0,0,128)"}}' },
		           { sheet:1, name: 'sparklineChart2', ftype: 'cdt',
				    json: '{"name": "minichart", "rng":[{"span":[1,4,9,4,9],"type":1}], "opt":{"base":{"span":[1,4,7,9,7],"type":1},"type":"column","pc":"orange","nc":"orange"}}' }
		        ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 
		            {sheet: 1, row: 2, col: 2, json: {data: "Generate sparkline column chart", fw:"bold", fz:14 }},
		            
		            {sheet: 1, row: 4, col: 4, json: { data: "Column bar"}}, 
	            	{sheet: 1, row: 4, col: 2, json: { data: -2}}, 
					{sheet: 1, row: 5, col: 2, json: { data: 2}}, 
					{sheet: 1, row: 6, col: 2, json: { data: 5}},
					{sheet: 1, row: 7, col: 2, json: { data: -3}},
					{sheet: 1, row: 8, col: 2, json: { data: 10}},
					{sheet: 1, row: 9, col: 2, json: { data: 3}},
					{sheet: 1, row: 4, col: 9, json: { data: "Column bar"}}, 
	            	{sheet: 1, row: 4, col: 7, json: { data: -2}}, 
					{sheet: 1, row: 5, col: 7, json: { data: 2}}, 
					{sheet: 1, row: 6, col: 7, json: { data: 5}},
					{sheet: 1, row: 7, col: 7, json: { data: -3}},
					{sheet: 1, row: 8, col: 7, json: { data: 10}},
					{sheet: 1, row: 9, col: 7, json: { data: 3}}
	            ]
			},
			
			/**
			 * This is the chart json example to show how to create sparkline by calling EnterpiseSheet API.
			 */
			featureSparklineWinLossJson : {
				fileName: 'Sparkline win/loss chart',
		        sheets: [
	                {name: 'Column', id: 1, color: 'orange' }
	            ],
		        floatings: [
		           { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,10]" },
		           { sheet:1, name:"merge2", ftype:"meg", json:"[4,4,9,5]" },
		           { sheet:1, name:"merge3", ftype:"meg", json:"[4,9,9,10]" },
		           { sheet:1, name: 'sparklineChart1', ftype: 'cdt',
				    json: '{"name": "minichart", "rng":[{"span":[1,4,4,4,4],"type":1}], "opt":{"base":{"span":[1,4,2,9,2],"type":1},"type":"gainloss","pc":"rgb(248,105,107)","nc":"rgb(0,0,128)"}, "id": "sparklineChart1"}' },
		           { sheet:1, name: 'sparklineChart2', ftype: 'cdt',
				    json: '{"name": "minichart", "rng":[{"span":[1,4,9,4,9],"type":1}], "opt":{"base":{"span":[1,4,7,9,7],"type":1},"type":"gainloss","pc":"orange","nc":"gray"}, "id": "sparklineChart2"}' }
		        ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 
		            {sheet: 1, row: 2, col: 2, json: {data: "Generate sparkline column chart", fw:"bold", fz:14 }},
		            
		            {sheet: 1, row: 4, col: 4, json: { data: "Win/loss"}}, 
	            	{sheet: 1, row: 4, col: 2, json: { data: -2}}, 
					{sheet: 1, row: 5, col: 2, json: { data: 2}}, 
					{sheet: 1, row: 6, col: 2, json: { data: 5}},
					{sheet: 1, row: 7, col: 2, json: { data: -3}},
					{sheet: 1, row: 8, col: 2, json: { data: 10}},
					{sheet: 1, row: 9, col: 2, json: { data: 3}},
					{sheet: 1, row: 4, col: 9, json: { data: "Win/loss"}}, 
	            	{sheet: 1, row: 4, col: 7, json: { data: -2}}, 
					{sheet: 1, row: 5, col: 7, json: { data: 2}}, 
					{sheet: 1, row: 6, col: 7, json: { data: 5}},
					{sheet: 1, row: 7, col: 7, json: { data: -3}},
					{sheet: 1, row: 8, col: 7, json: { data: 10}},
					{sheet: 1, row: 9, col: 7, json: { data: 3}}
	            ]
			},
			
			/**
			 * This is the line json example to show how to create sparkline by calling EnterpiseSheet API.
			 */
			featureSparklineLineJson : {
				fileName: 'Sparkline line chart',
		        sheets: [
	                {name: 'Column', id: 1, color: 'orange' }
	            ],
		        floatings: [
		           { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,10]" },
		           { sheet:1, name:"merge2", ftype:"meg", json:"[4,4,9,5]" },
		           { sheet:1, name:"merge3", ftype:"meg", json:"[4,9,9,10]" },
		           { sheet:1, name: 'sparklineChart1', ftype: 'cdt',
				    json: '{name:"minichart", rng:[{span:[1,4,4,4,4],"type":1}], opt:{base:{"span":[1,4,2,9,2],type:1},type:"line",sc:"rgb(248,105,107)"}, id: "sparklineChart1"}' },
		           { sheet:1, name: 'sparklineChart2', ftype: 'cdt',
				    json: '{name: "minichart", rng:[{span:[1,4,9,4,9],"type":1}], opt:{base:{"span":[1,4,7,9,7],type:1},type:"line",sc:"orange"}, id: "sparklineChart2"}' }
		        ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 
		            {sheet: 1, row: 2, col: 2, json: {data: "Generate sparkline line chart", fw:"bold", fz:14 }},
		            
	            	{sheet: 1, row: 4, col: 2, json: { data: -2}}, 
					{sheet: 1, row: 5, col: 2, json: { data: 2}}, 
					{sheet: 1, row: 6, col: 2, json: { data: 5}},
					{sheet: 1, row: 7, col: 2, json: { data: -3}},
					{sheet: 1, row: 8, col: 2, json: { data: 10}},
					{sheet: 1, row: 9, col: 2, json: { data: 3}},
	            	{sheet: 1, row: 4, col: 7, json: { data: -2}}, 
					{sheet: 1, row: 5, col: 7, json: { data: 2}}, 
					{sheet: 1, row: 6, col: 7, json: { data: 5}},
					{sheet: 1, row: 7, col: 7, json: { data: -3}},
					{sheet: 1, row: 8, col: 7, json: { data: 10}},
					{sheet: 1, row: 9, col: 7, json: { data: 3}}
	            ]
			},
			
			/**
			 * This is the example Json Data for apply table template to the cell range
			 * See running example @
			 *     EnterpriseSheet Samples / APIs -> Apply table 
			 */
			featureTableJson : {
				fileName: 'Apply table template',
		        sheets: [
	                {name: 'Table', id: 1, color: 'red' },
	            ],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" },
	            ],
		        cells: [
				    {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
				    {sheet: 1, row: 0, col: 2, json: {width: 100}},
				    {sheet: 1, row: 0, col: 3, json: {width: 100}},
				    {sheet: 1, row: 0, col: 4, json: {width: 100}},
				    {sheet: 1, row: 0, col: 6, json: {width: 100}},
				    {sheet: 1, row: 0, col: 7, json: {width: 100}},
				    {sheet: 1, row: 0, col: 8, json: {width: 100}},
			        {sheet: 1, row: 2, col: 2, json: {data: "Apply table template to the cell range", fw:"bold", fz:14 }}, 		         
				    
				    {sheet: 1, row: 4, col: 2, json: { data: "CATEGORY", tpl: '{id: "tpl_10", span: [1,4,2,7,4]}', trigger: false }},
				    {sheet: 1, row: 4, col: 3, json: { data: "ESTIMATED", tpl: '{id: "tpl_10", span: [1,4,2,7,4]}', trigger: false }},
				    {sheet: 1, row: 4, col: 4, json: { data: "ACTUAL", tpl: '{id: "tpl_10", span: [1,4,2,7,4]}', trigger: false }},
				    {sheet: 1, row: 5, col: 2, json: { data: "Bouquets", tpl: '{id: "tpl_10", span: [1,4,2,7,4]}'}},
				    {sheet: 1, row: 5, col: 3, json: { data: "500", tpl: '{id: "tpl_10", span: [1,4,2,7,4]}'}},
				    {sheet: 1, row: 5, col: 4, json: { data: "450", tpl: '{id: "tpl_10", span: [1,4,2,7,4]}' }},
				    {sheet: 1, row: 6, col: 2, json: { data: "Boutonnires", tpl: '{id: "tpl_10", span: [1,4,2,7,4]}' } },
				    {sheet: 1, row: 6, col: 3, json: { data: "200", tpl: '{id: "tpl_10", span: [1,4,2,7,4]}' }},
				    {sheet: 1, row: 6, col: 4, json: { data: "150", tpl: '{id: "tpl_10", span: [1,4,2,7,4]}' }},
		            {sheet: 1, row: 7, col: 2, json: { data: "Corsages", tpl: '{id: "tpl_10", span: [1,4,2,7,4]}'}},
		            {sheet: 1, row: 7, col: 3, json: { data: "100", tpl: '{id: "tpl_10", span: [1,4,2,7,4]}'}},
		            {sheet: 1, row: 7, col: 4, json: { data: "80", tpl: '{id: "tpl_10", span: [1,4,2,7,4]}' }},
		            
		            {sheet: 1, row: 10, col: 2, json: { data: "CATEGORY", tpl: '{id: "tpl_1", span: [1,10,2,13,4]}', trigger: false  }},
				    {sheet: 1, row: 10, col: 3, json: { data: "ESTIMATED", tpl: '{id: "tpl_1", span: [1,10,2,13,4]}', trigger: false }},
				    {sheet: 1, row: 10, col: 4, json: { data: "ACTUAL", tpl: '{id: "tpl_1", span: [1,10,2,13,4]}', trigger: false  }},
				    {sheet: 1, row: 11, col: 2, json: { data: "Bouquets", tpl: '{id: "tpl_1", span: [1,10,2,13,4]}'}},
				    {sheet: 1, row: 11, col: 3, json: { data: "500", tpl: '{id: "tpl_1", span: [1,10,2,13,4]}'}},
				    {sheet: 1, row: 11, col: 4, json: { data: "450", tpl: '{id: "tpl_1", span: [1,10,2,13,4]}' }},
				    {sheet: 1, row: 12, col: 2, json: { data: "Boutonnires", tpl: '{id: "tpl_1", span: [1,10,2,13,4]}' } },
				    {sheet: 1, row: 12, col: 3, json: { data: "200", tpl: '{id: "tpl_1", span: [1,10,2,13,4]}' }},
				    {sheet: 1, row: 12, col: 4, json: { data: "150", tpl: '{id: "tpl_1", span: [1,10,2,13,4]}' }},
		            {sheet: 1, row: 13, col: 2, json: { data: "Corsages", tpl: '{id: "tpl_1", span: [1,10,2,13,4]}'}},
		            {sheet: 1, row: 13, col: 3, json: { data: "100", tpl: '{id: "tpl_1", span: [1,10,2,13,4]}'}},
		            {sheet: 1, row: 13, col: 4, json: { data: "80", tpl: '{id: "tpl_1", span: [1,10,2,13,4]}' }},
		            
		            {sheet: 1, row: 16, col: 2, json: { data: "CATEGORY", tpl: '{id: "tpl_59", span: [1,16,2,19,4]}', trigger: false  }},
				    {sheet: 1, row: 16, col: 3, json: { data: "ESTIMATED", tpl: '{id: "tpl_59", span: [1,16,2,19,4]}', trigger: false }},
				    {sheet: 1, row: 16, col: 4, json: { data: "ACTUAL", tpl: '{id: "tpl_59", span: [1,16,2,19,4]}', trigger: false  }},
				    {sheet: 1, row: 17, col: 2, json: { data: "Bouquets", tpl: '{id: "tpl_59", span: [1,16,2,19,4]}'}},
				    {sheet: 1, row: 17, col: 3, json: { data: "500", tpl: '{id: "tpl_59", span: [1,16,2,19,4]}'}},
				    {sheet: 1, row: 17, col: 4, json: { data: "450", tpl: '{id: "tpl_59", span: [1,16,2,19,4]}' }},
				    {sheet: 1, row: 18, col: 2, json: { data: "Boutonnires", tpl: '{id: "tpl_59", span: [1,16,2,19,4]}' } },
				    {sheet: 1, row: 18, col: 3, json: { data: "200", tpl: '{id: "tpl_59", span: [1,16,2,19,4]}' }},
				    {sheet: 1, row: 18, col: 4, json: { data: "150", tpl: '{id: "tpl_59", span: [1,16,2,19,4]}' }},
		            {sheet: 1, row: 19, col: 2, json: { data: "Corsages", tpl: '{id: "tpl_59", span: [1,16,2,19,4]}'}},
		            {sheet: 1, row: 19, col: 3, json: { data: "100", tpl: '{id: "tpl_59", span: [1,16,2,19,4]}'}},
		            {sheet: 1, row: 19, col: 4, json: { data: "80", tpl: '{id: "tpl_59", span: [1,16,2,19,4]}' }},
		            
		            {sheet: 1, row: 4, col: 6, json: { data: "CATEGORY", tpl: '{id: "tpl_20", span: [1,4,6,7,8]}' }},
				    {sheet: 1, row: 4, col: 7, json: { data: "ESTIMATED", tpl: '{id: "tpl_20", span: [1,4,6,7,8]}'}},
				    {sheet: 1, row: 4, col: 8, json: { data: "ACTUAL", tpl: '{id: "tpl_20", span: [1,4,6,7,8]}' }},
				    {sheet: 1, row: 5, col: 6, json: { data: "Bouquets", tpl: '{id: "tpl_20", span: [1,4,6,7,8]}'}},
				    {sheet: 1, row: 5, col: 7, json: { data: "500", tpl: '{id: "tpl_20", span: [1,4,6,7,8]}'}},
				    {sheet: 1, row: 5, col: 8, json: { data: "450", tpl: '{id: "tpl_20", span: [1,4,6,7,8]}' }},
				    {sheet: 1, row: 6, col: 6, json: { data: "Boutonnires", tpl: '{id: "tpl_20", span: [1,4,6,7,8]}' } },
				    {sheet: 1, row: 6, col: 7, json: { data: "200", tpl: '{id: "tpl_20", span: [1,4,6,7,8]}' }},
				    {sheet: 1, row: 6, col: 8, json: { data: "150", tpl: '{id: "tpl_20", span: [1,4,6,7,8]}' }},
		            {sheet: 1, row: 7, col: 6, json: { data: "Corsages", tpl: '{id: "tpl_20", span: [1,4,6,7,8]}'}},
		            {sheet: 1, row: 7, col: 7, json: { data: "100", tpl: '{id: "tpl_20", span: [1,4,6,7,8]}'}},
		            {sheet: 1, row: 7, col: 8, json: { data: "80", tpl: '{id: "tpl_20", span: [1,4,6,7,8]}' }},
		            
		            {sheet: 1, row: 10, col: 6, json: { data: "CATEGORY", tpl: '{id: "tpl_30", span: [1,10,6,13,8]}' }},
				    {sheet: 1, row: 10, col: 7, json: { data: "ESTIMATED", tpl: '{id: "tpl_30", span: [1,10,6,13,8]}'}},
				    {sheet: 1, row: 10, col: 8, json: { data: "ACTUAL", tpl: '{id: "tpl_30", span: [1,10,6,13,8]}' }},
				    {sheet: 1, row: 11, col: 6, json: { data: "Bouquets", tpl: '{id: "tpl_30", span: [1,10,6,13,8]}'}},
				    {sheet: 1, row: 11, col: 7, json: { data: "500", tpl: '{id: "tpl_30", span: [1,10,6,13,8]}'}},
				    {sheet: 1, row: 11, col: 8, json: { data: "450", tpl: '{id: "tpl_30", span: [1,10,6,13,8]}' }},
				    {sheet: 1, row: 12, col: 6, json: { data: "Boutonnires", tpl: '{id: "tpl_30", span: [1,10,6,13,8]}' } },
				    {sheet: 1, row: 12, col: 7, json: { data: "200", tpl: '{id: "tpl_30", span: [1,10,6,13,8]}' }},
				    {sheet: 1, row: 12, col: 8, json: { data: "150", tpl: '{id: "tpl_30", span: [1,10,6,13,8]}' }},
		            {sheet: 1, row: 13, col: 6, json: { data: "Corsages", tpl: '{id: "tpl_30", span: [1,10,6,13,8]}'}},
		            {sheet: 1, row: 13, col: 7, json: { data: "100", tpl: '{id: "tpl_30", span: [1,10,6,13,8]}'}},
		            {sheet: 1, row: 13, col: 8, json: { data: "80", tpl: '{id: "tpl_30", span: [1,10,6,13,8]}' }},
		            
		            {sheet: 1, row: 16, col: 6, json: { data: "CATEGORY" }},
				    {sheet: 1, row: 16, col: 7, json: { data: "ESTIMATED"}},
				    {sheet: 1, row: 16, col: 8, json: { data: "ACTUAL" }},
				    {sheet: 1, row: 17, col: 6, json: { data: "Bouquets"}},
				    {sheet: 1, row: 17, col: 7, json: { data: "500"}},
				    {sheet: 1, row: 17, col: 8, json: { data: "450" }},
				    {sheet: 1, row: 18, col: 6, json: { data: "Boutonnires" } },
				    {sheet: 1, row: 18, col: 7, json: { data: "200" }},
				    {sheet: 1, row: 18, col: 8, json: { data: "150" }},
		            {sheet: 1, row: 19, col: 6, json: { data: "Corsages"}},
		            {sheet: 1, row: 19, col: 7, json: { data: "100"}},
		            {sheet: 1, row: 19, col: 8, json: { data: "80" }}
			    ]
			},
			
			filterCellsJson : { 
				fileName: 'Filter cell ranges',
		        sheets: [
	                {name: 'filter', id: 1, color: 'red' }
	            ],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,4]" },
	            ],
		        cells: [
				    {sheet: 1, row: 2, col: 0, json: {height: 30}}, 
				    {sheet: 1, row: 3, col: 0, json: {height: 30}}, 
				    {sheet: 1, row: 0, col: 2, json: {width: 150}},
				    {sheet: 1, row: 0, col: 3, json: {width: 150}},
				    {sheet: 1, row: 0, col: 4, json: {width: 100}},
				    {sheet: 1, row: 0, col: 6, json: {width: 150}},
			        {sheet: 1, row: 2, col: 2, json: {data: "Apply filter to the cell range", fw:"bold", fz:14 }}, 
		            
		            {sheet: 1, row: 3, col: 6, json: { data: "Filter1" } },
		            {sheet: 1, row: 4, col: 6, json: { data: "100" } },
		            {sheet: 1, row: 5, col: 6, json: { data: "test" } },
		            {sheet: 1, row: 6, col: 6, json: { data: "150" } },
		            {sheet: 1, row: 7, col: 6, json: { data: "200" } },
		            {sheet: 1, row: 8, col: 6, json: { data: "test" } },
		            {sheet: 1, row: 3, col: 7, json: { data: "Filter2" } },
		            {sheet: 1, row: 4, col: 7, json: { data: "ok" } },
		            {sheet: 1, row: 5, col: 7, json: { data: "test" } },
		            {sheet: 1, row: 6, col: 7, json: { data: "ok" } },
		            {sheet: 1, row: 8, col: 7, json: { data: "ok" } },
		            
		            {sheet: 1, row: 3, col: 2, json: { data: "Click and add filter", it: "button", btnStyle: "color: #FFF; background-color: #900;font-size: 105%;", onBtnClickFn: "BTN_ADD_FILTER_CALLBACK_FN" } },	
		            {sheet: 1, row: 3, col: 3, json: { data: "Remove filter", it: "button", btnStyle: "color: #FFF; background-color: #900;font-size: 105%;", onBtnClickFn: "BTN_REMOVE_FILTER_CALLBACK_FN" } }	
			    ]
			},
			
			filterTabsJson : { 
				fileName: 'Filter switch tab',
		        sheets: [
	                {name: 'filter tab1', id: 1, color: 'red' }, {name: 'filter tab2', id: 2, color: 'green' }
	            ],
		        cells: [
		            {sheet: 1, row: 3, col: 6, json: { data: "Filter1" } },
		            {sheet: 1, row: 4, col: 6, json: { data: "100" } },
		            {sheet: 1, row: 5, col: 6, json: { data: "test" } },
		            {sheet: 1, row: 6, col: 6, json: { data: "150" } },
		            {sheet: 1, row: 7, col: 6, json: { data: "200" } },
		            {sheet: 1, row: 8, col: 6, json: { data: "test" } },
		            {sheet: 2, row: 3, col: 7, json: { data: "Filter2" } },
		            {sheet: 2, row: 4, col: 7, json: { data: "ok" } },
		            {sheet: 2, row: 5, col: 7, json: { data: "test" } },
		            {sheet: 2, row: 6, col: 7, json: { data: "ok" } },
		            {sheet: 2, row: 8, col: 7, json: { data: "ok" } }
			    ]
			},
			
			sortCellsJson : { 
				fileName: 'Sort cell ranges',
		        sheets: [
	                {name: 'Sort', id: 1, color: 'red' }
	            ],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,4]" },
	            ],
		        cells: [
				    {sheet: 1, row: 2, col: 0, json: {height: 30}}, 	
				    {sheet: 1, row: 10, col: 0, json: {height: 30}},
				    {sheet: 1, row: 12, col: 0, json: {height: 30}},
				    {sheet: 1, row: 0, col: 2, json: {width: 150}},
				    {sheet: 1, row: 0, col: 3, json: {width: 150}},
				    {sheet: 1, row: 0, col: 4, json: {width: 150}},
				    {sheet: 1, row: 0, col: 6, json: {width: 150}},
			        {sheet: 1, row: 2, col: 2, json: {data: "Apply sort to the cell range", fw:"bold", fz:14 }}, 

			        {sheet: 1, row: 3, col: 2, json: { data: "Sort by number" } },
		            {sheet: 1, row: 4, col: 2, json: { data: "100" } },
		            {sheet: 1, row: 5, col: 2, json: { data: "20" } },
		            {sheet: 1, row: 6, col: 2, json: { data: "150" } },
		            {sheet: 1, row: 7, col: 2, json: { data: "60" } },
		            {sheet: 1, row: 8, col: 2, json: { data: "300" } },
		            
		            {sheet: 1, row: 10, col: 2, json: { data: "Sort ascending", it: "button", btnStyle: "color: #FFF; background-color: #900;font-size: 105%;", onBtnClickFn: "BTN_SORT_ASC_CALLBACK_FN" } },	
		            {sheet: 1, row: 12, col: 2, json: { data: "Sort descending", it: "button", btnStyle: "color: #FFF; background-color: #900;font-size: 105%;", onBtnClickFn: "BTN_SORT_DESC_CALLBACK_FN" } },
		            
		            {sheet: 1, row: 3, col: 4, json: { data: "Sort by String" } },
		            {sheet: 1, row: 4, col: 4, json: { data: "abc" } },
		            {sheet: 1, row: 5, col: 4, json: { data: "hij" } },
		            {sheet: 1, row: 6, col: 4, json: { data: "efg" } },
		            {sheet: 1, row: 7, col: 4, json: { data: "kzh" } },
		            {sheet: 1, row: 8, col: 4, json: { data: "yyy" } },
		            
		            {sheet: 1, row: 10, col: 4, json: { data: "Sort ascending", it: "button", btnStyle: "color: #FFF; background-color: #900;font-size: 105%;", onBtnClickFn: "BTN_SORT_ASC_CALLBACK_FN_2" } },	
		            {sheet: 1, row: 12, col: 4, json: { data: "Sort descending", it: "button", btnStyle: "color: #FFF; background-color: #900;font-size: 105%;", onBtnClickFn: "BTN_SORT_DESC_CALLBACK_FN_2" } }	
			    ]
			},
			
			/**
			 * validation feature
			 */
			featureValidationJson : {
				fileName: 'validation json file',
		        sheets: [
	                {name: 'First', id: 1, color: 'red' }
	            ],
	            floatings: [
					{sheet:1, name:"validation1",ftype:"cdt",json: "{name:\"vd\",rng:[{span:[1,2,2,4,4],type:1}],opt:{dt:0,op:0,min:120,max:150,hint:\"Between 120 and 150\",allow:true,ignoreBlank:true},id:\"vd-1\"}"}
				],
		        cells: [
				    {sheet: 1, row: 2, col: 2, json: {data:100}},
				    {sheet: 1, row: 2, col: 3, json: {data:200}},
					{sheet: 1, row: 2, col: 4, json: {data:300}},
				   // {sheet: 1, row: 3, col: 2, json: {data:120}},
					{sheet: 1, row: 3, col: 3, json: {data:220}},
				    {sheet: 1, row: 3, col: 4, json: {data:150}},
		            {sheet: 1, row: 4, col: 2, json: {data:130}},
					{sheet: 1, row: 4, col: 3, json: {data:170}},
				    {sheet: 1, row: 4, col: 4, json: {data:110}}
			    ]
			},
			
			featureValidationNumberJson : {
				fileName: 'Number validation',
		        sheets: [
	                {name: 'number validation', id: 1, color: 'red' }
	            ],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" },
					{sheet:1, name:"validation1",ftype:"cdt",
						json: "{name:\"vd\",rng:[{span:[1,4,2,300,80],type:1}],opt:{dt:0,op:0,min:120,max:150,hint:\"Between 120 and 150\",allow:true,ignoreBlank:true},id:\"vd-1\"}"}
				],
		        cells: [
				    {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
			        {sheet: 1, row: 2, col: 2, json: {data: "Add number validation to the table", fw:"bold", fz:14 }}, 		

				    {sheet: 1, row: 4, col: 2, json: {data:100}},
			        {sheet: 1, row: 4, col: 3, json: {data:200}},
			        {sheet: 1, row: 4, col: 4, json: {data:300}},
			        {sheet: 1, row: 5, col: 2, json: {data:120}},
			        {sheet: 1, row: 5, col: 3, json: {data:220}},
			        //{sheet: 1, row: 5, col: 4, json: {data:150}},
			        {sheet: 1, row: 6, col: 2, json: {data:130}},
			        {sheet: 1, row: 6, col: 3, json: {data:170}},
			        {sheet: 1, row: 6, col: 4, json: {data:110}}
			    ]
			},
			
			featureValidationTextJson : {
				fileName: 'Text validation',
		        sheets: [
	                {name: 'Text', id: 1, color: 'red' }
	            ],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" },
					{sheet:1, name:"validation1",ftype:"cdt", json: "{name:\"vd\",rng:[{span:[1,4,2,6,4],type:1}],opt:{dt:1,op:8,txt:\"ok\",hint:\"Need contain ok\",allow:true},id:\"vd-2\"}"}
				],
		        cells: [
		            {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
			        {sheet: 1, row: 2, col: 2, json: {data: "Add number validation to the table", fw:"bold", fz:14 }}, 
			        
			         {sheet: 1, row: 4, col: 2, json: {data:"ok"}},
			         {sheet: 1, row: 4, col: 3, json: {data:"test"}},
			         {sheet: 1, row: 4, col: 4, json: {data:"that is ok"}},
			         {sheet: 1, row: 5, col: 2, json: {data:"ok123"}},
			         {sheet: 1, row: 5, col: 3, json: {data:"fine"}},
			         {sheet: 1, row: 5, col: 4, json: {data:"cool"}},
			         {sheet: 1, row: 6, col: 2, json: {data:"me too"}},
			         {sheet: 1, row: 6, col: 3, json: {data:"cool"}},
			         {sheet: 1, row: 6, col: 4, json: {data:"too"}}
			    ]
			},
			
			featureValidationDateJson : {
				fileName: 'Date validation',
		        sheets: [
	                {name: 'Date', id: 1, color: 'red' }
	            ],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" },
					{sheet:1, name:"validation1",ftype:"cdt", json: "{name:\"vd\",rng:[{span:[1,4,2,6,4],type:1}],opt:{dt:2,op:0,mind:\"2015-01-09\",maxd:\"2015-01-12\",hint:\"Date between\",allow:true},id:\"vd-1\"}"}
				],
		        cells: [
		            {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
			        {sheet: 1, row: 2, col: 2, json: {data: "Add Date validation to the table", fw:"bold", fz:14 }},    
		                
			         {sheet: 1, row: 4, col: 2, json: {data:"2015-01-11",fm:"date",dfm:"Y-m-d"}},
			         {sheet: 1, row: 4, col: 3, json: {data:"2015-01-12",fm:"date",dfm:"Y-m-d"}},
			         {sheet: 1, row: 4, col: 4, json: {data:"2015-02-01",fm:"date",dfm:"Y-m-d"}},
			         {sheet: 1, row: 5, col: 2, json: {data:"2015-01-01",fm:"date",dfm:"Y-m-d"}},
			         {sheet: 1, row: 5, col: 3, json: {data:"2015-01-17",fm:"date",dfm:"Y-m-d"}},
			         {sheet: 1, row: 5, col: 4, json: {data:"2015-01-15",fm:"date",dfm:"Y-m-d"}},
			         {sheet: 1, row: 6, col: 2, json: {data:"2015-01-10",fm:"date",dfm:"Y-m-d"}},
			         {sheet: 1, row: 6, col: 3, json: {data:"2015-12-30",fm:"date",dfm:"Y-m-d"}},
			         {sheet: 1, row: 6, col: 4, json: {data:"2015-01-06",fm:"date",dfm:"Y-m-d"}}
			    ]
			},
			
			featureValidationListJson : {
				fileName: 'List item validation',
		        sheets: [
	                {name: 'List', id: 1, color: 'red' }
	            ],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" },
					{sheet:1, name:"validation1",ftype:"cdt", json: "{name:\"vd\",rng:[{span:[1,4,2,5,4],type:1}],opt:{dt:3,op:0,list:[\"Apple\",\"Orange\",\"Banana\",\"Kiwi\"],hint:\"Fruit\",allow:true},id:\"vd-1\"}"}
				],
		        cells: [
		            {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
			        {sheet: 1, row: 2, col: 2, json: {data: "Add Date validation to the table", fw:"bold", fz:14 }}, 
			        
			         {sheet: 1, row: 4, col: 2, json: {data:"ok"}},
			         {sheet: 1, row: 4, col: 3, json: {data:"apple"}},
			         {sheet: 1, row: 4, col: 4, json: {data:"orange"}},
			         {sheet: 1, row: 5, col: 2, json: {data:"test"}},
			         {sheet: 1, row: 5, col: 3, json: {data:"banana"}},
			         {sheet: 1, row: 5, col: 4, json: {data:"kiwi"}}
			    ]
			},
			
			/**
			 * on cell blur function
			 */
			callbackCellBlurJson : {
				fileName: 'onCellBlur call back',
	            sheets: [{name: 'cell blur', id: 1, color: 'red' }],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" },
	                { sheet:1, name:"merge2", ftype:"meg", json:"[11,2,11,8]" },
	                { sheet:1, name:"merge3", ftype:"meg", json:"[12,2,12,8]" },
				],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
	                {sheet: 1, row: 11, col: 0, json: {height:30, hoff:0}}, 
	                {sheet: 1, row: 12, col: 0, json: {height: 90, hoff:0}}, 
			        {sheet: 1, row: 2, col: 2, json: {data: "Cell onBlur event callback", fw:"bold", fz:14 }}, 
			        {sheet: 1, row: 4, col: 2, json: {data: "1) Defined a callback fn, such as: CELL_ON_BLUR_CALLBACK_FN"}},
			        {sheet: 1, row: 5, col: 2, json: {data: "2) add onCellBlurFn: \"CELL_ON_BLUR_CALLBACK_FN\" in the cell json data structure"}},
	            	{sheet: 1, row: 6, col: 2, json: {data: "3) \"CELL_ON_BLUR_CALLBACK_FN\" will return 3 parameter: cell value, row index, column index"}},
	            	{sheet: 1, row: 8, col: 2, json: {data: "Do cell onBlur in here to test", onCellBlurFn: "CELL_ON_BLUR_CALLBACK_FN", fw:"bold"}},
	            	{sheet: 1, row: 11, col: 2, json: {data: "Event listener function", fw:"bold", fz:14 }},
	            	{sheet: 1, row: 12, col: 2, json: {data: "var sheet = SHEET_API_HD.sheet; \n var editor = sheet.getEditor(); \n editor.on('quit', function(editor, sheetId, row, col) { \n \u00a0 \u00a0 \u00a0  console.log('sheetId: = ' + sheetId + ', row : '+row+', col : '+col); \n}, this);"}}
	            ]
			},
			
			// the example code for quit edit process ...
			callbackCellBlurCode : function(){
				var sheet = SHEET_API_HD.sheet;
				var editor = sheet.getEditor();
				editor.on('quit', function(editor, sheetId, row, col){
					console.log('sheetId: = ' + sheetId + ', row : '+row+', col : '+col);
				}, this);
			},
			
			/**
			 * on cell focus function
			 */
			callbackCellFocusJson : {
				fileName: 'onCellFocus call back',
	            sheets: [{name: 'cell focus', id: 1, color: 'red' }],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" },
				],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
			        {sheet: 1, row: 2, col: 2, json: {data: "Cell onFocus event callback", fw:"bold", fz:14 }}, 
			        {sheet: 1, row: 4, col: 2, json: {data: "1) Defined a callback fn, such as: CELL_ON_FOCUS_CALLBACK_FN"}},
			        {sheet: 1, row: 5, col: 2, json: {data: "2) add onCellFocusFn: \"CELL_ON_FOCUS_CALLBACK_FN\" in the cell json data structure"}},
	            	{sheet: 1, row: 6, col: 2, json: {data: "3) \"CELL_ON_FOCUS_CALLBACK_FN\" will return 3 parameter: cell value, row index, column index"}},
	            	{sheet: 1, row: 8, col: 2, json: {data: "Do cell onFocus in here to test", onCellFocusFn: "CELL_ON_FOCUS_CALLBACK_FN", fw:"bold"}}
	            ]
			},
			
			/**
			 * on cell click function
			 */
			callbackCellClickJson : {
				fileName: 'onCellClick call back',
	            sheets: [{name: 'cell click', id: 1, color: 'red' }],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" },
				],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
			        {sheet: 1, row: 2, col: 2, json: {data: "Cell click event callback", fw:"bold", fz:14 }}, 
			        {sheet: 1, row: 4, col: 2, json: {data: "1) Defined a callback fn, such as: CELL_CLICK_CALLBACK_FN"}},
			        {sheet: 1, row: 5, col: 2, json: {data: "2) add onCellClickFn: \"CELL_CLICK_CALLBACK_FN\" in the cell json data structure"}},
	            	{sheet: 1, row: 6, col: 2, json: {data: "3) \"CELL_CLICK_CALLBACK_FN\" will return 3 parameter: cell value, row index, column index"}},
	            	{sheet: 1, row: 8, col: 2, json: {data: "Do cell click in here to test", onCellClickFn: "CELL_CLICK_CALLBACK_FN", fw:"bold"}}
	            ]
			},
			
			/**
			 * on cell double click function
			 */
			callbackCellDblClickJson : {
				fileName: 'onCellDoubleClick call back',
	            sheets: [{name: 'cell double click', id: 1, color: 'red' }],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" },
				],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
			        {sheet: 1, row: 2, col: 2, json: {data: "Cell double click event callback", fw:"bold", fz:14 }}, 
			        {sheet: 1, row: 4, col: 2, json: {data: "1) Defined a callback fn, such as: CELL_DOUBLE_CLICK_CALLBACK_FN"}},
			        {sheet: 1, row: 5, col: 2, json: {data: "2) add onCellDoubleClickFn: \"CELL_DOUBLE_CLICK_CALLBACK_FN\" in the cell json data structure"}},
	            	{sheet: 1, row: 6, col: 2, json: {data: "3) \"CELL_DOUBLE_CLICK_CALLBACK_FN\" will return 3 parameter: cell value, row index, column index"}},
	            	{sheet: 1, row: 8, col: 2, json: {data: "Do cell double click in here to test", onCellDoubleClickFn: "CELL_DOUBLE_CLICK_CALLBACK_FN", fw:"bold"}}
	            ]
			},
			
			/**
			 * on mouse move function
			 */
			callbackMouseDownJson : {
				fileName: 'onMouseDown call back',
	            sheets: [{name: 'mouse down', id: 1, color: 'red' }],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" },
				],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
			        {sheet: 1, row: 2, col: 2, json: {data: "Cell mouse down event callback", fw:"bold", fz:14 }}, 
			        {sheet: 1, row: 4, col: 2, json: {data: "1) Defined a callback fn, such as: CELL_MOUSE_DOWN_CALLBACK_FN"}},
			        {sheet: 1, row: 5, col: 2, json: {data: "2) add onCellMouseDownFn: \"CELL_MOUSE_DOWN_CALLBACK_FN\" in the cell json data structure"}},
	            	{sheet: 1, row: 6, col: 2, json: {data: "3) \"CELL_MOUSE_DOWN_CALLBACK_FN\" will return 3 parameter: cell value, row index, column index"}},
	            	{sheet: 1, row: 8, col: 2, json: {data: "Do mouse down in here to test", onCellMouseDownFn: "CELL_MOUSE_DOWN_CALLBACK_FN", fw:"bold"}}
	            ]
			},
			
			/**
			 * on cell mouse move fn
			 */
			callbackMouseMoveJson : {
				fileName: 'onMouseMove call back',
	            sheets: [{name: 'mouse move', id: 1, color: 'red' }],
			    floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" },
				],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 	
			        {sheet: 1, row: 2, col: 2, json: {data: "Cell mouse move event callback", fw:"bold", fz:14 }}, 
			        {sheet: 1, row: 4, col: 2, json: {data: "1) Defined a callback fn, such as: CELL_MOUSE_MOVE_CALLBACK_FN"}},
			        {sheet: 1, row: 5, col: 2, json: {data: "2) add onCellMouseMoveFn: \"CELL_MOUSE_MOVE_CALLBACK_FN\" in the cell json data structure"}},
	            	{sheet: 1, row: 6, col: 2, json: {data: "3) \"CELL_MOUSE_MOVE_CALLBACK_FN\" will return 3 parameter: cell value, row index, column index"}},
	            	{sheet: 1, row: 8, col: 2, json: {data: "Do mouse move in here to test", onCellMouseMoveFn: "CELL_MOUSE_MOVE_CALLBACK_FN", fw:"bold"}}
	            ]
			},
			
			/**
			 * This is the checkbox/Radio json example to show how to create them by calling EnterpiseSheet API.
			 */
			featureCheckboxJson : {
				fileName: 'Checkbox file',
		        sheets: [
	                {name: 'checkbox', id: 1, color: 'orange' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" }
	            ],
		        cells: [
		            {sheet: 1, row: 0, col: 2, json: {width: 100}}, 
		            {sheet: 1, row: 0, col: 5, json: {width: 100}}, 
		            {sheet: 1, row: 0, col: 9, json: {width: 100}}, 
		            {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 
		            {sheet: 1, row: 2, col: 2, json: {data: "Dispaly your data with Checkbox / Radio button", fw:"bold", fz:14 }}, 
		            
				    { sheet: 1, row: 4, col: 2, json: { data: "Favorite fruit:" } },
				    { sheet: 1, row: 5, col: 2,  json: { data: "Banana", it: "checkbox",  itn: "fruit", itchk: false, itchkpos: "right"}},
				    { sheet: 1, row: 6, col: 2, json: { data: "Apple", it: "checkbox", itn: "fruit", itchk: true }},
				    { sheet: 1, row: 7, col: 2, json: { data: "Orange", it: "checkbox", itn: "fruit", itchk: false } },
				    
				    { sheet: 1, row: 4, col: 4, json: { data: "Check only" } },
				    { sheet: 1, row: 5, col: 4, json: { data: "Align centre" } },
				    { sheet: 1, row: 6, col: 4, json: { data: "Align right" } },
				    { sheet: 1, row: 4, col: 5, json: { it: "checkbox", itn: "checkonly", itchk: true } },
				    { sheet: 1, row: 5, col: 5, json: { it: "checkbox", itn: "checkonly", itchk: false, ta: "center" } },
				    { sheet: 1, row: 6, col: 5, json: { it: "checkbox", itn: "checkonly", itchk: false, ta: "right" } },
				    { sheet: 1, row: 7, col: 5, json: { data: "Middle", it: "checkbox", itn: "checkonly", itchk: false, ta: "center" } },
				    { sheet: 1, row: 8, col: 5, json: { data: "Indent", it: "checkbox", itn: "checkonly", itchk: false, ti: "10" } },
				    
				    { sheet: 1, row: 4, col: 7, json: { data: "Most favorite sport:" } },
				    { sheet: 1, row: 5, col: 7, json: { data: "Soccer", it: "radio", itn: "sports", itchk: true, itchkpos: "right" }},
				    { sheet: 1, row: 6, col: 7, json: { data: "Basketball", it: "radio", itn: "sports", itchk: false}},
				    { sheet: 1, row: 7, col: 7, json: { data: "Ski", it: "radio", itn: "sports", itchk: false}},
				    
				    { sheet: 1, row: 4, col: 9, json: { data: "Check only" } },
				    { sheet: 1, row: 5, col: 9, json: { data: "Align centre" } },
				    { sheet: 1, row: 6, col: 9, json: { data: "Align right" } },
				    { sheet: 1, row: 4, col: 10, json: { it: "radio", itn: "radioOnly", itchk: true } },
				    { sheet: 1, row: 5, col: 10, json: { it: "radio", itn: "radioOnly", itchk: false, ta: "center" } },
				    { sheet: 1, row: 6, col: 10, json: { it: "radio", itn: "radioOnly", itchk: false, ta: "right" } },
				    { sheet: 1, row: 7, col: 10, json: { data: "Middle", it: "radio", itn: "radioOnly", itchk: false, ta: "center" } },
				    { sheet: 1, row: 8, col: 10, json: { data: "Indent", it: "radio", itn: "radioOnly", itchk: false, ti: "10" } }
			    ]
			},
			
			/**
			 * This is the checkbox/Radio json example to show how to create them by calling EnterpiseSheet API.
			 */
			featureButtonJson : {
				fileName: 'Button cells',
		        sheets: [
	                {name: 'Buttons', id: 1, color: 'orange' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" }
	            ],
		        cells: [
		            {sheet: 1, row: 0, col: 2, json: {width: 200}}, 
		            {sheet: 1, row: 0, col: 5, json: {width: 100}}, 
		            {sheet: 1, row: 0, col: 9, json: {width: 100}}, 
		            {sheet: 1, row: 0, col: 0, json: {height: 30}}, 
		            {sheet: 1, row: 2, col: 2, json: {data: "Add button to the cell", fw:"bold", fz:14 }}, 
		            
				    { sheet: 1, row: 4, col: 2, json: { data: "Click me!", it: "button", btnStyle: "color: #900; font-weight: bold; padding:5px 25px;border-radius:19px;-moz-border-radius:19px;-webkit-border-radius:19px;", onBtnClickFn: "CUSTOM_BUTTON_CLICK_CALLBACK_FN" } },
				    { sheet: 1, row: 5, col: 2, json: { data: "Click me!", it: "button", btnStyle: "color: #900; font-weight: bold; font-size: 150%; text-transform: uppercase;", onBtnClickFn: "CUSTOM_BUTTON_CLICK_CALLBACK_FN" } },
				    { sheet: 1, row: 6, col: 2, json: { data: "Click me!", it: "button", btnStyle: "color: #FFF; background-color: #900;", onBtnClickFn: "CUSTOM_BUTTON_CLICK_CALLBACK_FN" } },
				    { sheet: 1, row: 7, col: 2, json: { data: "Click me!", it: "button", btnStyle: "color: #900; border: 1px solid #900; font-weight: bold;", onBtnClickFn: "CUSTOM_BUTTON_CLICK_CALLBACK_FN" } },
				    { sheet: 1, row: 8, col: 2, json: { data: "Click me!", it: "button", btnStyle: "color: #900; text-transform: uppercase; margin-left: 20px;", onBtnClickFn: "CUSTOM_BUTTON_CLICK_CALLBACK_FN" } },
				    { sheet: 1, row: 9, col: 2, json: { data: "Click me!", it: "button", btnStyle: "color: #900; text-transform: uppercase; margin-top: 10px;", onBtnClickFn: "CUSTOM_BUTTON_CLICK_CALLBACK_FN" } },
				    { sheet: 1, row: 10, col: 2, json: { data: "Click me!", it: "button", btnStyle: "color: #900; border: 1px solid #900; font-weight: bold; width: 150px;", onBtnClickFn: "CUSTOM_BUTTON_CLICK_CALLBACK_FN" } },
			    ]
			},
			
			/**
			 * This is the combobox json example to show how to create them by calling EnterpiseSheet API.
			 */
			featureComboboxJson : {
                fileName: 'Combobox cell',
	            sheets: [
	                {name: 'Combobox Example', id: 1, color: 'red' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,5]" }
	            ],
	            cells: [
	                {sheet: 1, row: 0, col: 7, json: {width: 100, drop: Ext.encode({data: ["天","CPC","CPM","次"]}) }}, 
	                {sheet: 1, row: 0, col: 9, json: {width: 100, drop: Ext.encode({data: ["天天","CPC1","CPM1","次天"]}) }}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 150}}, 
		            {sheet: 1, row: 0, col: 5, json: {width: 100}}, 
		            {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}}, 
		            {sheet: 1, row: 2, col: 2, json: {data: "Dispaly drop-down list with combobox", fw:"bold", fz:14 }}, 
		            
		            { sheet: 1, row: 4, col: 2, json: {data: "Combobox",}}, 
		            { sheet: 1, row: 4, col: 4, json: {data: "Value",}}, 
			        { sheet: 1, row: 6, col: 2, json: {data: "Monday", drop: Ext.encode({data: "Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday"})} },
			        { sheet: 1, row: 6, col: 4, json: {data: "=b6", cal: true } },
			        { sheet: 1, row: 8, col: 2, json: {data: "5", drop: Ext.encode({data: "2,5,10,20,50"})} },
			        { sheet: 1, row: 8, col: 4, json: {data: "=b8", cal: true } }
			    ]
		    },
		    
		    /**
			 * This is the link json example to show how to create them by calling EnterpiseSheet API.
			 */
		    featureLinkJson : {
		    	fileName: 'Link',
	            sheets: [
	                {name: 'link', id: 1, color: 'red' }, {name: 'link2', id: 2, color: 'orange' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" }
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 150}}, 
		            {sheet: 1, row: 2, col: 2, json: {data: "Hyperlink function", fw:"bold", fz:14 }},
		            
			        { sheet: 1, row: 4, col: 2, json: { data: "link", link: "www.enterpriseSheet.com" } },
                    { sheet: 1, row: 6, col: 2, json: { data: "www.google.com", link: "www.google.com"} },
                    { sheet: 1, row: 8, col: 2, json: { data: "link to another cell", link: "=$B$12", fm: "link"} },
                    { sheet: 1, row: 10, col: 2, json: { data: "link to another tab", link: "=link2!$B$12", fm: "link"} },
                    { sheet: 1, row: 12, col: 2, json: { data: "jump to here"} },
                    { sheet: 2, row: 12, col: 2, json: { data: "jump to here"} },
			    ]
		    },
		    
		    /**
		     * money format
		     */
		    featureMoneyJson : {
		    	fileName: 'Money format',
	            sheets: [
	                {name: 'First', id: 1, color: 'red' }, {  name: 'Second', id: 2 }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" }
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 150}}, 
	                {sheet: 1, row: 0, col: 4, json: {width: 150}}, 
	                {sheet: 1, row: 0, col: 6, json: {width: 150}}, 
		            {sheet: 1, row: 2, col: 2, json: {data: "Money Format", fw:"bold", fz:14 }},
		            
		            { sheet: 1, row: 4, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 4, col: 4, json: { data: "$#,##0.00" } },
			        { sheet: 1, row: 4, col: 6, json: { data: "12345.678", fm: "money|$|2|none"} },
			        { sheet: 1, row: 5, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 5, col: 4, json: { data: "RMB#,##0.00" } },
			        { sheet: 1, row: 5, col: 6, json: { data: "12345.678", fm: "money|RMB|2|none"} },
			        { sheet: 1, row: 6, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 6, col: 4, json: { data: "$#,##0.0" } },
			        { sheet: 1, row: 6, col: 6, json: { data: "12345.678", fm: "money|$|1|none"} },
			        { sheet: 1, row: 7, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 7, col: 4, json: { data: String.fromCharCode(165) + "#,##0.0" } },
			        { sheet: 1, row: 7, col: 6, json: { data: "12345.678", fm: "money|&#x00a5;|1|none"} },
			        { sheet: 1, row: 8, col: 2, json: { data: "-12345.678" } },
		            { sheet: 1, row: 8, col: 4, json: { data: "-$#,##0.00"} },
			        { sheet: 1, row: 8, col: 6, json: { data: "-12345.678", fm: "money|$|2|negative1"} },
			        { sheet: 1, row: 9, col: 2, json: { data: "-12345.678" } },
		            { sheet: 1, row: 9, col: 4, json: { data: "$#,##0.00", color: "red" } },
			        { sheet: 1, row: 9, col: 6, json: { data: "-12345.678", fm: "money|$|2|negative2"} },
			        { sheet: 1, row: 10, col: 2, json: { data: "-12345.678" } },
		            { sheet: 1, row: 10, col: 4, json: { data: "-$#,##0.00", color: "red" } },
			        { sheet: 1, row: 10, col: 6, json: { data: "-12345.678", fm: "money|$|2|negative3"} },
			        { sheet: 1, row: 11, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 11, col: 4, json: { data: "$#,##0.000000" } },
			        { sheet: 1, row: 11, col: 6, json: { data: "12345.678", fm: "money|$|6|none"} },
			        { sheet: 1, row: 12, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 12, col: 4, json: { data:  String.fromCharCode(8361) + "#,##0.00" } },
			        { sheet: 1, row: 12, col: 6, json: { data: "12345.678", fm: "money|" + String.fromCharCode(8361) + "|2|none"} },
			        { sheet: 1, row: 13, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 13, col: 4, json: { data: "R$#,##0.00" } },
			        { sheet: 1, row: 13, col: 6, json: { data: "12345.678", fm: "money|R$|2|none"} },
			        { sheet: 1, row: 14, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 14, col: 4, json: { data:  String.fromCharCode(8364) + "#,##0.00" } },
			        { sheet: 1, row: 14, col: 6, json: { data: "12345.678", fm: "money|" + String.fromCharCode(8364) + "|2|none"} },
			        { sheet: 1, row: 15, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 15, col: 4, json: { data:  String.fromCharCode(1547) + "#,##0.00" } },
			        { sheet: 1, row: 15, col: 6, json: { data: "12345.678", fm: "money|" + String.fromCharCode(1547) + "|2|none"} },
			        { sheet: 1, row: 16, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 16, col: 4, json: { data:  String.fromCharCode(8369) + "#,##0.00" } },
			        { sheet: 1, row: 16, col: 6, json: { data: "12345.678", fm: "money|" + String.fromCharCode(8369) + "|2|none"} },
			        { sheet: 1, row: 17, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 17, col: 4, json: { data:  String.fromCharCode(163) + "#,##0.00" } },
			        { sheet: 1, row: 17, col: 6, json: { data: "12345.678", fm: "money|" + String.fromCharCode(163) + "|2|none"} },
			        { sheet: 1, row: 18, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 18, col: 4, json: { data:  String.fromCharCode(65020) + "#,##0.00" } },
			        { sheet: 1, row: 18, col: 6, json: { data: "12345.678", fm: "money|" + String.fromCharCode(65020) + "|2|none"} },
			        { sheet: 1, row: 19, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 19, col: 4, json: { data:  String.fromCharCode(8377) + "#,##0.00" } },
			        { sheet: 1, row: 19, col: 6, json: { data: "12345.678", fm: "money|" + String.fromCharCode(8377) + "|2|none"} },
			        { sheet: 1, row: 20, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 20, col: 4, json: { data:  String.fromCharCode(8362) + "#,##0.00" } },
			        { sheet: 1, row: 20, col: 6, json: { data: "12345.678", fm: "money|" + String.fromCharCode(8362) + "|2|none"} },
			        { sheet: 1, row: 21, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 21, col: 4, json: { data:  String.fromCharCode(163) + "#,##0.00" } },
			        { sheet: 1, row: 21, col: 6, json: { data: "12345.678", fm: "money|" + String.fromCharCode(163) + "|2|none"} },
			        { sheet: 1, row: 22, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 22, col: 4, json: { data:  String.fromCharCode(8372) + "#,##0.00" } },
			        { sheet: 1, row: 22, col: 6, json: { data: "12345.678", fm: "money|" + String.fromCharCode(8372) + "|2|none"} },
			        { sheet: 1, row: 23, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 23, col: 4, json: { data:  String.fromCharCode(8356) + "#,##0.00" } },
			        { sheet: 1, row: 23, col: 6, json: { data: "12345.678", fm: "money|" + String.fromCharCode(8356) + "|2|none"} },
			        { sheet: 1, row: 24, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 24, col: 4, json: { data:  String.fromCharCode(322) + "#,##0.00" } },
			        { sheet: 1, row: 24, col: 6, json: { data: "12345.678", fm: "money|" + String.fromCharCode(322) + "|2|none"} },
			        { sheet: 1, row: 25, col: 2, json: { data: "12345.678" } },
		            { sheet: 1, row: 25, col: 4, json: { data:  String.fromCharCode(75) + String.fromCharCode(269) + "#,##0.00" } },
			        { sheet: 1, row: 25, col: 6, json: { data: "12345.678", fm: "money|" + String.fromCharCode(75) + String.fromCharCode(269) + "|2|none"} },
			    ]
		    },
		    
		    /**
		     * date format
		     */
		    featureDateJson : {
		    	fileName: 'Date format',
	            sheets: [
	                {name: 'date', id: 1, color: 'red' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" }
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 150}}, 
	                {sheet: 1, row: 0, col: 4, json: {width: 250}}, 
	                {sheet: 1, row: 0, col: 6, json: {width: 250}}, 
		            {sheet: 1, row: 2, col: 2, json: {data: "Date Format", fw:"bold", fz:14 }},

		            { sheet: 1, row: 4, col: 2, json: { data: "Y/m/d" } },
		            { sheet: 1, row: 4, col: 4, json: { data: "yyyy/MM/dd" } },
			        { sheet: 1, row: 4, col: 6, json: { data: "2015-01-27", fm: "date", dfm: "Y/m/d"} },
			        { sheet: 1, row: 5, col: 2, json: { data: "Y-m-d" } },
			        { sheet: 1, row: 5, col: 4, json: { data: "yyyy-MM-dd" } },
		            { sheet: 1, row: 5, col: 6, json: { data: "2015-01-27", fm: "date", dfm: "Y-m-d"} },
		            { sheet: 1, row: 6, col: 2, json: { data: "MMMMM dd, yyyy" } },
		            { sheet: 1, row: 6, col: 4, json: { data: "Y/m/d" } },
		            { sheet: 1, row: 6, col: 6, json: { data: "2015-01-27", fm: "date", dfm: "F d, Y"} },
		            { sheet: 1, row: 7, col: 2, json: { data: "M d, Y" } },
		            { sheet: 1, row: 7, col: 4, json: { data: "MMM dd, yyyy" } },
		            { sheet: 1, row: 7, col: 6, json: { data: "2015-01-27", fm: "date", dfm: "M d, Y"} },
		            { sheet: 1, row: 8, col: 2, json: { data: "d-m-y" } },
		            { sheet: 1, row: 8, col: 4, json: { data: "dd-MM-yyyy" } },
		            { sheet: 1, row: 8, col: 6, json: { data: "2015-01-27", fm: "date", dfm: "d-m-y"} },
		            { sheet: 1, row: 9, col: 2, json: { data: "d-M-y" } },
		            { sheet: 1, row: 9, col: 4, json: { data: "dd-MMM-yyy" } },
		            { sheet: 1, row: 9, col: 6, json: { data: "2015-01-27", fm: "date", dfm: "d-M-y"} },
		            { sheet: 1, row: 10, col: 2, json: { data: "l, M d, Y" } },
		            { sheet: 1, row: 10, col: 4, json: { data: "dddd, MMM dd, yyyy" } },
		            { sheet: 1, row: 10, col: 6, json: { data: "2015-01-27", fm: "date", dfm: "l, M d, Y"} },
			    ]
		    },
		    
		    /**
		     * custom number format
		     */
		    featureCustomJson : {
		    	fileName: 'Custom number format',
	            sheets: [
	                {name: 'custom', id: 1, color: 'red' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" }
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 150}}, 
	                {sheet: 1, row: 0, col: 4, json: {width: 350}}, 
	                {sheet: 1, row: 0, col: 6, json: {width: 200}}, 
		            {sheet: 1, row: 2, col: 2, json: {data: "Custom number Format", fw:"bold", fz:14 }},
		            
		            { sheet: 1, row: 4, col: 2, json: { data: "-12323.45678" } },
		            { sheet: 1, row: 4, col: 4, json: { data: "[magenta]RMB#,##0.00;[green]-RMB#,##0.00;[red]zero;@" } },
			        { sheet: 1, row: 4, col: 6, json: { data: "-12323.45678", fm: "number", dfm: "[magenta]RMB#,##0.00;[green]-RMB#,##0.00;[red]zero;@" } },
			        { sheet: 1, row: 5, col: 2, json: { data: "0" } },
				    { sheet: 1, row: 5, col: 6, json: { data: "0", fm: "number", dfm: "[magenta]RMB#,##0.00;[green]-RMB#,##0.00;[red]zero;@" } },
			        { sheet: 1, row: 6, col: 2, json: { data: "12323.45678" } },
			        { sheet: 1, row: 6, col: 6, json: { data: "12323.45678", fm: "number", dfm: "[magenta]RMB#,##0.00;[green]-RMB#,##0.00;[red]zero;@" } },
			        { sheet: 1, row: 7, col: 2, json: { data: "-12323.45678" } },
			        { sheet: 1, row: 7, col: 4, json: { data: "[Blue]#,##0.00;[Red]-#,##0.00;[yellow]zero;@" } },		       
			        { sheet: 1, row: 7, col: 6, json: { data: "-12323.45678", fm: "number", dfm: "[Blue]#,##0.00;[Red]-#,##0.00;[yellow]zero;@" } },
			        { sheet: 1, row: 8, col: 2, json: { data: "0" } },	       
			        { sheet: 1, row: 8, col: 6, json: { data: "0", fm: "number", dfm: "[Blue]#,##0.00;[Red]-#,##0.00;[yellow]zero;@" } },
			        { sheet: 1, row: 9, col: 2, json: { data: "12323.45678" } },	       
			        { sheet: 1, row: 9, col: 6, json: { data: "12323.45678", fm: "number", dfm: "[Blue]#,##0.00;[Red]-#,##0.00;[yellow]zero;@" } },
			        
			        { sheet: 1, row: 10, col: 2, json: { data: "-12323.45678" } },
			        { sheet: 1, row: 10, col: 4, json: { data: "$#,##0.00;[Red]-$#,##0.00;[Red]ZERO" } },
			        { sheet: 1, row: 10, col: 6, json: { data: "-12323.45678", fm: "number", dfm: "$#,##0.00;[Red]-$#,##0.00;[Red]ZERO" } },
			        { sheet: 1, row: 11, col: 2, json: { data: "-12323.45678" } },
			        { sheet: 1, row: 11, col: 4, json: { data: "#,##0.00;[Red]-#,##0.00" } },
			        { sheet: 1, row: 11, col: 6, json: { data: "-12323.45678", fm: "number", dfm: "#,##0.00;[Red]-#,##0.00" } },
			        { sheet: 1, row: 12, col: 2, json: { data: "-12323.45678" } },
			        { sheet: 1, row: 12, col: 4, json: { data: "# ??/??" } },
			        { sheet: 1, row: 12, col: 6, json: { data: "-12323.45678", fm: "number", dfm: "# ??/??" }},
			        { sheet: 1, row: 13, col: 2, json: { data: "-12323.45678" } },
			        { sheet: 1, row: 13, col: 4, json: { data: "# ??/16" } },
			        { sheet: 1, row: 13, col: 6, json: { data: "-12323.45678", fm: "number", dfm: "# ??/16" }},
			        { sheet: 1, row: 14, col: 2, json: { data: "-12323.45678" } },
			        { sheet: 1, row: 14, col: 4, json: { data: "0.00%" } },
			        { sheet: 1, row: 14, col: 6, json: { data: "-12323.45678", fm: "number", dfm: "0.00%" }},
			        { sheet: 1, row: 15, col: 2, json: { data: "-12323.45678" } },
			        { sheet: 1, row: 15, col: 4, json: { data: "0.0e+00" } },
			        { sheet: 1, row: 15, col: 6, json: { data: "-12323.45678", fm: "number", dfm: "0.0e+00" }},
			        { sheet: 1, row: 16, col: 2, json: { data: "-12323.45678" } },
			        { sheet: 1, row: 16, col: 4, json: { data: "#,##0.00RMB;-#,##0.00RMB;zero;@" } },
			        { sheet: 1, row: 16, col: 6, json: { data: "-12323.45678", fm: "number", dfm: "#,##0.00RMB;-#,##0.00RMB;zero;@" }},
			        { sheet: 1, row: 17, col: 2, json: { data: "-12323.45678" } },
			        { sheet: 1, row: 17, col: 4, json: { data: "#,##0.00000000" } },
			        { sheet: 1, row: 17, col: 6, json: { data: "-12323.45678", fm: "number", dfm: "#,##0.00000000" }},	        
			    ]
		    },
		    
		    featureFormulaJson : {
		    	fileName: 'Formula',
	            sheets: [
	                {name: 'formula', id: 1, color: 'red' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" }
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height:30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 350}}, 
		            {sheet: 1, row: 2, col: 2, json: {data: "Formula support", fw:"bold", fz:14 }},
		            
		            { sheet: 1, row: 4, col: 2, json: { data: "Formula Types", tpl: '{id: "tpl_13", span: [1,4,2,13,2]}', trigger: false} },
			        { sheet: 1, row: 5, col: 2, json: { data: "Numeric / Mathematical Functions", tpl: '{id: "tpl_13", span: [1,4,2,13,2]}'} },
			        { sheet: 1, row: 6, col: 2, json: { data: "Logic Functions", tpl: '{id: "tpl_13", span: [1,4,2,13,2]}'} },
			        { sheet: 1, row: 7, col: 2, json: { data: "Lookup / Reference Functions", tpl: '{id: "tpl_13", span: [1,4,2,13,2]}'} },
			        { sheet: 1, row: 8, col: 2, json: { data: "Statistical Functions", tpl: '{id: "tpl_13", span: [1,4,2,13,2]}'} },
			        { sheet: 1, row: 9, col: 2, json: { data: "String Functions", tpl: '{id: "tpl_13", span: [1,4,2,13,2]}'} },
			        { sheet: 1, row: 10, col: 2, json: { data: "Date Functions", tpl: '{id: "tpl_13", span: [1,4,2,13,2]}'} },
			        { sheet: 1, row: 11, col: 2, json: { data: "Financial Functions", tpl: '{id: "tpl_13", span: [1,4,2,13,2]}'} },
			        { sheet: 1, row: 12, col: 2, json: { data: "Information Functions", tpl: '{id: "tpl_13", span: [1,4,2,13,2]}'} },
			        { sheet: 1, row: 13, col: 2, json: { data: "Engineering Functions", tpl: '{id: "tpl_13", span: [1,4,2,13,2]}'} },
			        
			        { sheet: 1, row: 4, col: 4, json: { data: 1 } },
				    { sheet: 1, row: 5, col: 4, json: { data: 2 } },
				    { sheet: 1, row: 6, col: 4, json: { data: 3 } },
				    { sheet: 1, row: 7, col: 4, json: { data: 4 } },
				    { sheet: 1, row: 8, col: 4, json: { data: 5 } },
				    { sheet: 1, row: 9, col: 4, json: { data: 6 } },
				    { sheet: 1, row: 10, col: 4, json: { data: 7 } },
				    { sheet: 1, row: 11, col: 4, json: { data: 8 } },
				    { sheet: 1, row: 4, col: 5, json: { data: 1 } },
				    { sheet: 1, row: 5, col: 5, json: { data: 2 } },
				    { sheet: 1, row: 6, col: 5, json: { data: 3 } },
				    { sheet: 1, row: 7, col: 5, json: { data: 4 } },
				    { sheet: 1, row: 8, col: 5, json: { data: 5 } },
				    { sheet: 1, row: 9, col: 5, json: { data: 6 } },
				    { sheet: 1, row: 10, col: 5, json: { data: 7 } },
				    { sheet: 1, row: 11, col: 5, json: { data: 8 } },
				    { sheet: 1, row: 4, col: 6, json: { data: 1 } },
				    { sheet: 1, row: 5, col: 6, json: { data: 2 } },
				    { sheet: 1, row: 6, col: 6, json: { data: 3 } },
				    { sheet: 1, row: 7, col: 6, json: { data: 4 } },
				    { sheet: 1, row: 8, col: 6, json: { data: 5 } },
				    { sheet: 1, row: 9, col: 6, json: { data: 6 } },
				    { sheet: 1, row: 10, col: 6, json: { data: 7 } },
				    { sheet: 1, row: 11, col: 6, json: { data: 8 } },
			        
			        { sheet: 1, row: 15, col: 2, json: { data: "=2^3 & \" times\""}},
			        { sheet: 1, row: 15, col: 4, json: { data: "=2^3 & \" times\"", cal: true}},
			        { sheet: 1, row: 16, col: 2, json: { data: "=IF(ISNA(VLOOKUP(D4,$F$4:$F$11,1,FALSE)), \"No\", \"Yes\")"}},
			        { sheet: 1, row: 16, col: 4, json: { data: "=IF(ISNA(VLOOKUP(D4,$F$4:$F$11,1,FALSE)), \"No\", \"Yes\")", cal: true}},
			        { sheet: 1, row: 17, col: 2, json: { data: "=count(D4:E11,F4:F11)"}},
			        { sheet: 1, row: 17, col: 4, json: { data: "=count(D4:E11,F4:F11)", cal: true}},
			        { sheet: 1, row: 18, col: 2, json: { data: "=sum(D4:E11,F4:F11)"}},
			        { sheet: 1, row: 18, col: 4, json: { data: "=sum(D4:E11,F4:F11)", cal: true}},
			        { sheet: 1, row: 19, col: 2, json: { data: "=IF(ISERROR(D4/F11),\"NA\",D4/F11)"}},
			        { sheet: 1, row: 19, col: 4, json: { data: "=IF(ISERROR(D4/F11),\"NA\",D4/F11)", cal: true}},
			        { sheet: 1, row: 20, col: 2, json: { data: "=today()+5"}},
			        { sheet: 1, row: 20, col: 4, json: { data: "=today()+5", cal: true}},
			        { sheet: 1, row: 21, col: 2, json: { data: "=code(UPPER(D5))"}},
			        { sheet: 1, row: 21, col: 4, json: { data: "=code(UPPER(D5))", cal: true}},
			        { sheet: 1, row: 22, col: 2, json: { data: "=CONVERT(6,\"C\",\"F\")"}},
			        { sheet: 1, row: 22, col: 4, json: { data: "=CONVERT(6,\"C\",\"F\")", cal: true}},
			        { sheet: 1, row: 23, col: 2, json: { data: "=min(D4:F11)"}},
			        { sheet: 1, row: 23, col: 4, json: { data: "=min(D4:F11)", cal: true}},
			        { sheet: 1, row: 24, col: 2, json: { data: "=if(D4>0, if(D5=0, 12, 15), \"OK\")"}},
			        { sheet: 1, row: 24, col: 4, json: { data: "=if(D4>0, if(D5=0, 12, 15), \"OK\")", cal: true}},
			        { sheet: 1, row: 25, col: 2, json: { data: "=CONCATENATE(\"Tory\", \" \", \"John\")"}},
			        { sheet: 1, row: 25, col: 4, json: { data: "=CONCATENATE(\"Tory\", \" \", \"John\")", cal: true}},
			        { sheet: 1, row: 26, col: 2, json: { data: "=DDB(10000, 5000, 5, 1)"}},
			        { sheet: 1, row: 26, col: 4, json: { data: "=DDB(10000, 5000, 5, 1)", cal: true}},
			        { sheet: 1, row: 27, col: 2, json: { data: "=COUNTBLANK(J19:K26)"}},
			        { sheet: 1, row: 27, col: 4, json: { data: "=COUNTBLANK(J19:K26)", cal: true}},
			        { sheet: 1, row: 28, col: 2, json: { data: "=sumproduct(D4:E11*(F4:F11>2))"}},
			        { sheet: 1, row: 28, col: 4, json: { data: "=sumproduct(D4:E11*(F4:F11>2))", cal: true}},
			        
			    ]
		    },
		    
		    featureFormulaNmgrJson : {
		    	fileName: 'Formula name mgr',
	            sheets: [
	                {name: 'name mgr', id: 1, color: 'red' }
	            ],
	            fileConfig: [
	               //{name: "rangeData", ctype: "ref", json: "[{span: [1,4,2,6,2], type: 1}]" },
	               {name: "rangeData", ctype: "named_func", json: "[{\"cal\":\"'name mgr'!$B$4:$C$6\"}]" }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" }
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 200}},
		            {sheet: 1, row: 2, col: 2, json: {data: "Defined a name manager range", fw:"bold", fz:14 }},
		            
		            { sheet: 1, row: 4, col: 2, json: { data: "2" } },
			        { sheet: 1, row: 5, col: 2, json: { data: "3" } },
			        { sheet: 1, row: 6, col: 2, json: { data: "4" } },
			        { sheet: 1, row: 4, col: 3, json: { data: "5" } },
			        { sheet: 1, row: 5, col: 3, json: { data: "6" } },
			        { sheet: 1, row: 6, col: 3, json: { data: "7" } },
			        
			        { sheet: 1, row: 8, col: 2, json: { data: "Defined: rangeData" } },
			        { sheet: 1, row: 8, col: 3, json: { data: "B4:C6" } },
			        { sheet: 1, row: 9, col: 2, json: { data: "sum(rangeData, 1)" } },
			        { sheet: 1, row: 9, col: 3, json: { data: "=sum(rangeData, 1)", cal: "true" } },
			        { sheet: 1, row: 10, col: 2, json: { data: "average(rangeData)" } },
			        { sheet: 1, row: 10, col: 3, json: { data: "=average(rangeData)", cal: "true" } },
		        ]
		    },
		    
		    featureFormulaNmgrJsonAdd : {
		    	fileName: 'Formula name mgr add',
	            sheets: [
	                {name: 'Sheet1', id: 1, color: 'red' }
	            ],
	            cells: [
		            { sheet: 1, row: 4, col: 2, json: { data: "2" } },
			        { sheet: 1, row: 5, col: 2, json: { data: "3" } },
			        { sheet: 1, row: 6, col: 2, json: { data: "4" } },
			        { sheet: 1, row: 4, col: 3, json: { data: "5" } },
			        { sheet: 1, row: 5, col: 3, json: { data: "6" } },
			        { sheet: 1, row: 6, col: 3, json: { data: "7" } },
		        ]
		    },
		    
		    featureCellAlignJson : {
		    	fileName: 'Set cell align and indent',
	            sheets: [
	                {name: 'First', id: 1, color: 'red' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" }
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}}, 
	                {sheet: 1, row: 9, col: 0, json: {height: 50, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 200}},
	                {sheet: 1, row: 0, col: 3, json: {width: 200}},
	                {sheet: 1, row: 0, col: 4, json: {width: 200}},
	                {sheet: 1, row: 11, col: 0, json: {autoHeight: 30}},
		            {sheet: 1, row: 2, col: 2, json: {data: "Cell align and indent", fw:"bold", fz:14 }},
		            
			        { sheet: 1, row: 4, col: 2, json: { data: "align right", ta: "right" } },
			        { sheet: 1, row: 5, col: 2, json: { data: "align center", ta: "center"} },
			        { sheet: 1, row: 6, col: 2, json: { data: "align left", ta: "left"} },
			        { sheet: 1, row: 7, col: 2, json: { data: "indent 10 px", ti: "10"} },
			        { sheet: 1, row: 8, col: 2, json: { data: "indent 20 px", ti: "20"} },
			        { sheet: 1, row: 9, col: 2, json: { data: "vertical align bottom", va: "bottom" } },
			        { sheet: 1, row: 9, col: 3, json: { data: "vertical align middle", va: "middle" } },
			        { sheet: 1, row: 9, col: 4, json: { data: "vertical align top", va: "top" } },
			        { sheet: 1, row: 9, col: 5, json: { data: "middle", va: "middle", ta: "center" } },
			        { sheet: 1, row: 11, col: 2, json: { data: "This used to prove long text WRAP method", ws: "normal", ww: "break-word" } },
			    ]
		    },
		    
		    featureCellFontJson : {
		    	fileName: 'Cell font',
	            sheets: [
	                {name: 'First', id: 1, color: 'red' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" }
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 200}},
	                {sheet: 1, row: 0, col: 3, json: {width: 200}},
	                {sheet: 1, row: 0, col: 4, json: {width: 200}},
	                {sheet: 1, row: 5, col: 0, json: {height: 30, hoff:0}}, 
	                {sheet: 1, row: 7, col: 0, json: {height: 40, hoff:0}}, 
		            {sheet: 1, row: 2, col: 2, json: {data: "Set Cell Font", fw:"bold", fz:14 }},
		            {sheet: 1, row: 11, col: 0, json: {height: 100}}, 
		            
			        { sheet: 1, row: 4, col: 2, json: { data: "bold", fw: "bold"  } },
			        { sheet: 1, row: 4, col: 3, json: { data: "Italic", fs: "italic" } },
			        { sheet: 1, row: 5, col: 2, json: { data: "Underline", u: "underline" } },
			        { sheet: 1, row: 5, col: 3, json: { data: "Strike", s: "line-through" } },
			        { sheet: 1, row: 5, col: 4, json: { data: "Overline", o: "overline" } },
			        { sheet: 1, row: 6, col: 2, json: { data: "Courier New", ff: "Courier New" } },
			        { sheet: 1, row: 6, col: 3, json: { data: "Lucida Console", ff: "Lucida Console" } },
			        { sheet: 1, row: 6, col: 4, json: { data: "verdana", ff: "verdana" } },
			        { sheet: 1, row: 7, col: 2, json: { data: "font size", fz: 14 } },
			        { sheet: 1, row: 7, col: 3, json: { data: "font size", fz: 20 } },
			        { sheet: 1, row: 7, col: 4, json: { data: "bold 25px Arial", fz: 25, fw: "bold", ff: "Arial" } },
			        { sheet: 1, row: 9, col: 2, json: { data: "Good", bgc:"rgb(198,239,206)", color:"rgb(0,97,0)" } },
			        { sheet: 1, row: 9, col: 3, json: { data: "OK", bgc:"rgb(255,235,156)", color:"rgb(156,101,0)" } },
			        { sheet: 1, row: 9, col: 4, json: { data: "Bad", bgc:"pink", color:"rgb(156,0,6)" } },		        
			        { sheet: 1, row: 11, col: 2, json: { data: "Vertical Text", rotation: 90 } },
			        { sheet: 1, row: 11, col: 3, json: { data: "Vertical Text 2", rotation: 270 } },
			    ]
		    },
		    
		    /**
			 * This is the example Json Data for show auto scroll function
			 * See running example @
			 *     EnterpriseSheet Samples / APIs -> Auto scroll
			 */
		    featureAutoScroll : {
		        fileName: 'Auto scroll',
	            sheets: [
	                {name: 'auto scroll', id: 1, color: 'red' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" },
	               { sheet:1, name:"merge2", ftype:"meg", json:"[4,2,4,6]" }
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 200}},
	                {sheet: 1, row: 0, col: 4, json: {width: 200}},
	                {sheet: 1, row: 4, col: 0, json: {height: 30, hoff:0}}, 
		            {sheet: 1, row: 2, col: 2, json: {data: "Auto scroll using mouse wheel and drag/drop", fw:"bold", fz:14 }},
		            
			        {sheet: 1, row: 4, col: 2, json: { data: "Using mouse wheel to achieve vertical navigate to different cell, and use drag and drop to achieve horizontal level cell navigate.", ws: "normal", ww: "break-word" } },
			        {sheet: 1, row: 5, col: 2, json: { data: "EnterpriseSheet allow your scroll up to 1024 columns and up to 1048576 rows." } },
			        {sheet: 1, row: 7, col: 2, json: { data: "Max allowed columns" } },
			        {sheet: 1, row: 7, col: 3, json: { data: "1024", fw: "bold", color: "orange" } },
			        {sheet: 1, row: 8, col: 2, json: { data: "Max allowed rows" } },
			        {sheet: 1, row: 8, col: 3, json: { data: "1048576", fw: "bold", color: "orange" } },
			        {sheet: 1, row: 10, col: 2, json: { data: "link to cell ALL1 (Column 1000)", link: "=ALL7", fm: "link"} },
			        {sheet: 1, row: 12, col: 2, json: { data: "link to cell B100000 (Row 100000)", link: "=B100000", fm: "link"} },
			        {sheet: 1, row: 14, col: 2, json: { data: "link to cell ALL100000 (Row 100000 and column 1000)", link: "=ALL100000", fm: "link"} },
			        {sheet: 1, row: 100000, col: 2, json: { data: "I am in row 100000" } },
			        {sheet: 1, row: 7, col: 1000, json: { data: "I am in column 1000" } },
			        {sheet: 1, row: 100000, col: 1000, json: { data: "I am in column 1000 and row 100000" } },
			    ]
		    },
		    
		    featureCellColorJson : {
		    	fileName: 'Cell Color',
	            sheets: [
	                {name: 'First', id: 1, color: 'red' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" }
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 300}},
		            {sheet: 1, row: 2, col: 2, json: {data: "Set Cell Color", fw:"bold", fz:14 }},
		            
			        { sheet: 1, row: 4, col: 2, json: { data: "Default color" } },
			        { sheet: 1, row: 6, col: 2, json: { data: "Blue foreColor", color: "blue" } },
			        { sheet: 1, row: 8, col: 2, json: { data: "Cyan backColor", bgc: "cyan" } },
			        { sheet: 1, row: 10, col: 2, json: { data: "Yellow text with green backColor", color: "yellow", bgc: "green" } },
			        { sheet: 1, row: 12, col: 0, json: { bgc: "#FBD5B5" } },
			        { sheet: 1, row: 0, col: 7, json: { bgc: "#FBD5B5" } },
			    ]
		    },
		    
		    sheetDefaultStyle : {
		    	fileName: 'Sheet Default Style',
	            sheets: [
	                {name: 'First', id: 1, color: 'red' }, {name: 'Second', id: 2, color: 'orange' }
	            ],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,5]" }
	            ],
	            cells: [
                    {sheet: 1, row: 0, col: 0, json: {bgc: "#999933", color: "orange", data: "ok", width:200, height: 30} },
		            {sheet: 1, row: 2, col: 2, json: {data: "Set Sheet Default style", fw:"bold", fz:14 }},
			    ]
		    },
		    
		    /**
		     * add show / hide rows 
		     */
		    featureRowColHideJson : {
		    	fileName: 'Hide Rows / Columns',
	            sheets: [
	                {name: 'First', id: 1, color: 'red' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" }
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 300}},
		            {sheet: 1, row: 2, col: 2, json: {data: "Hide or show columns / rows", fw:"bold", fz:14 }},
		            
			        { sheet: 1, row: 8, col: 0, json: { hidden: "true"} },
			        { sheet: 1, row: 9, col: 0, json: { hidden: "true"} },
			        { sheet: 1, row: 10, col: 0, json: { hidden: "true"} },
			        { sheet: 1, row: 0, col: 5, json: { hidden: "true"} },
			        { sheet: 1, row: 0, col: 6, json: { hidden: "true"} },
			    ]
		    },
		    
		    /**
		     * add color, heigh to row / column 
		     */
		    featureRowColColorJson : {
		    	fileName: 'Set color of col/row',
	            sheets: [
	                {name: 'First', id: 1, color: 'red' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" }
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 300}},
		            {sheet: 1, row: 2, col: 2, json: {data: "Set row/column height and color", fw:"bold", fz:14 }},
         
			        { sheet: 1, row: 12, col: 0, json: { bgc: "#FBD5B5", height: 100 } },
			        { sheet: 1, row: 0, col: 7, json: { bgc: "#FBD5B5", width: 300 } },
			    ]
		    },
		    
		    // add comments to cell
		    featureCommentJson : {
		    	fileName: 'Comments',
	            sheets: [ {name: 'Comments', id: 1, color: 'red' }, {name: 'Comment2', id: 2, color: 'red' }],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" }
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 300}},
		            {sheet: 1, row: 2, col: 2, json: {data: "Set comment to the cell", fw:"bold", fz:14 }},
         
			        { sheet: 1, row: 4, col: 2, json: { data: 'See editable comments', comment: 'Great work' } },
		            { sheet: 1, row: 5, col: 2, json: { data: 'Set read only comments', comment: 'Great work', commentEdit: "hide" } },
		            { sheet: 1, row: 6, col: 2, json: { data: 'Bold comments', comment: 'Great work', commentEdit: "hide", commentStyle: "b" } },
		            { sheet: 1, row: 7, col: 2, json: { data: 'Underline comments', comment: 'Great work', commentEdit: "hide", commentStyle: "u" } },
		            { sheet: 1, row: 9, col: 2, json: { data: 'Update comments - applyWay' } },
		            { sheet: 2, row: 1, col: 1, json: { data: 10 } },
		            { sheet: 2, row: 2, col: 1, json: { data: 10 } },
		            { sheet: 2, row: 2, col: 2, json: { data: '=sum(A1,A2)', cal: true, comment: 'Great work', commentEdit: "hide" } }
			    ]
		    },
		    
		    /**
		     * add group example to the sheet
		     */
		    featureGroup : {
		    	fileName: 'Group range',
	            sheets: [
	                {name: 'First', id: 1, color: 'red' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" },
	          //     { sheet:1, name:"rowGroups", ftype:"rowgroup", json: "[{level:3, span:[3,20]},{level:2, span:[7,17]},{level:1, span:[11,15]}]" },
	               { sheet:1, name:"colGroups", ftype:"colgroup", json: "[{level:3, span:[1,9]},{level:2, span:[2,7]},{level:1, span:[3,5]}]" },
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 300}},
		            {sheet: 1, row: 2, col: 2, json: {data: "Add group to the sheet", fw:"bold", fz:14 }},
		            
		            {sheet: 1, row: 4, col: 2, json: { data: "CATEGORY", tpl: '{id: "tpl_27", span: [1,4,2,7,4]}', trigger: false }},
				    {sheet: 1, row: 4, col: 3, json: { data: "ESTIMATED", tpl: '{id: "tpl_27", span: [1,4,2,7,4]}', trigger: false }},
				    {sheet: 1, row: 4, col: 4, json: { data: "ACTUAL", tpl: '{id: "tpl_27", span: [1,4,2,7,4]}', trigger: false }},
				    {sheet: 1, row: 5, col: 2, json: { data: "Bouquets", tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
				    {sheet: 1, row: 5, col: 3, json: { data: "500", tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
				    {sheet: 1, row: 5, col: 4, json: { data: "450", tpl: '{id: "tpl_27", span: [1,4,2,7,4]}' }},
				    {sheet: 1, row: 6, col: 2, json: { data: "Boutonnires", tpl: '{id: "tpl_27", span: [1,4,2,7,4]}' } },
				    {sheet: 1, row: 6, col: 3, json: { data: "200", tpl: '{id: "tpl_27", span: [1,4,2,7,4]}' }},
				    {sheet: 1, row: 6, col: 4, json: { data: "150", tpl: '{id: "tpl_27", span: [1,4,2,7,4]}' }},
		            {sheet: 1, row: 7, col: 2, json: { data: "Corsages", tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
		            {sheet: 1, row: 7, col: 3, json: { data: "100", tpl: '{id: "tpl_27", span: [1,4,2,7,4]}'}},
		            {sheet: 1, row: 7, col: 4, json: { data: "80", tpl: '{id: "tpl_27", span: [1,4,2,7,4]}' }},
			    ]
		    },
		    
		    featureDisableSheetJson : {
		    	fileName: 'Set sheet disable',
	            sheets: [
	                {name: 'First', id: 1, color: 'red' }, {  name: 'Second', id: 2 }
	            ],
	            cells: [{
			        sheet: 1, row: 2, col: 2, json: {data: "This is read only"}
			    },{
			        sheet: 1, row: 2, col: 3, json: {data: "OK 123"}
			    }]
		    },
		    
		    featureDisableJson : {
		    	fileName: 'Set cell/row/colum disable',
	            sheets: [
	                {name: 'First', id: 1, color: 'red' }, {  name: 'Second', id: 2 }
	            ],
	            cells: [{
			        sheet: 1, row: 0, col: 1, json: {dsd: "ed"}
			    },{
			        sheet: 1, row: 1, col: 0, json: {dsd: "ed"}
			    },{
			        sheet: 1, row: 3,  col: 3, json: {data: "not editable", dsd: "ed"}
			    }]
		    },
		    		    
		    featureCusomizedFormula : {
				'customerCal1': {
			        fn: function(){
			            var me = this.me;
			            var result = me.checkCoordValid(arguments);
			            if(true !== result) throw {code: 'CAL_INCORRECT_COORD',span: result};
			            var arr = [];

			            me.each(this.sheet, this.row, this.col, arguments, function(obj, itemType, index, insideIndex, item){
			                var data;
			                if('span' == itemType){
			                    var sheet = obj[0], row = obj[1], col = obj[2];
			                    var cell = me.getCellData(sheet, row, col, this);
			                    data = cell.data;
			                }else{
			                    data = obj;
			                }
			                if(Ext.isString(data) && 0 < data.length){
			                    var num = Number(data[0]);
			                    if(!Ext.isNumber(num)){
			                        arr.push(data[0].toUpperCase());
			                    }
			                }
			            }, this);
			            if(0 === arr.length){
			                throw {code: 'NEW_EXCEPTION_1'};
			            }
			
			            return arr.join('');
			        },
			        hint: ['customerCal1', 'customerCal1(str1, str2, ...)', 'Syntax: customerCal1()<br><br>Compose a new string by take the first letter of the passed items','info']
			    }
		    },
		    
		    featureCusomizedFormulaJson : {
		    	fileName: 'Customized formula',
	            sheets: [ {name: 'First', id: 1, color: 'red' }, {  name: 'Second', id: 2 }],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" },
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 200}},
		            {sheet: 1, row: 2, col: 2, json: {data: "Build your customized formula", fw:"bold", fz:14 }},

	                { sheet: 1, row: 4, col: 2, json: { data: "central" } },
	                { sheet: 1, row: 5, col: 2, json: { data: "intelligence" } },
	                { sheet: 1, row: 6, col: 2, json: { data: "agency" } },
	                { sheet: 1, row: 8, col: 2, json: { data: "=customerCal1(B4,B5,B6)" } },
	            ]
		    },
		    
		    featureInsertWidgetJson : {
		    	fileName: 'Insert Widget',
	            sheets: [ {name: 'First', id: 1, color: 'red' }, {  name: 'Second', id: 2 }],
	            cells: [
	                { sheet: 1, row: 2, col: 2, json: { data: "central" } }, 
	            ]
		    },
		    
		    featureAddRowJson : {
		    	fileName: 'Add new row',
	            sheets: [ {name: 'First', id: 1, color: 'red' }],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" },
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 200}},
		            {sheet: 1, row: 2, col: 2, json: {data: "Add 2 new row between 5 and 6", fw:"bold", fz:14 }},

	                { sheet: 1, row: 5, col: 2, json: { data: "Row 5" } },
	                { sheet: 1, row: 6, col: 2, json: { data: "Should be in Row 8 if insert new rows success" } }
	            ]
		    },
		    
		    featureAddColJson : {
		    	fileName: 'Add new column',
	            sheets: [ {name: 'First', id: 1, color: 'red' }],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,5]" },
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 200}},
		            {sheet: 1, row: 2, col: 2, json: {data: "Add 2 new columns between Column F and G", fw:"bold", fz:14 }},

	                { sheet: 1, row: 5, col: 6, json: { data: "Column F" } },
	                { sheet: 1, row: 5, col: 7, json: { data: "Should be in Column i if insert new columns success" } }
	            ]
		    },

		    // for group toggle event ...
		    groupToggleEventListenerCode : function(){
		    	var sheet = SHEET_API_HD.sheet;
		    	sheet.on({
			        scope: this,
			        /*
			         * @param {String} name: the name of the group
			         * @param {Boolean} expand: true means this group level is expanded, or it's collapsed
			         * @param {Integer} sheetId: the sheet id
			         * @param {Array} rows: the row index array of this row group level
			         * @param {Array} oldRowGroups: the old row group array before this action
			         * @param {Array} rowGroups: the row group array after this action
			         */
			        'togglerowgroup': function(name, expand, sheetId, rows, oldRowGroups, rowGroups){
			            alert((expand ? 'Expand' : 'Collapse')+(' row '+rows[0]+' to row '+rows[rows.length-1]))
			        },
			        /*
			         * @param {String} name: the name of the group
			         * @param {Boolean} expand: true means this group level is expanded, or it's collapsed
			         * @param {Integer} sheetId: the sheet id
			         * @param {Array} cols: the col index array of this col group level
			         * @param {Array} oldColGroups: the old col group array before this action
			         * @param {Array} colGroups: the col group array after this action
			         */
			        'togglecolgroup': function(name, expand, sheetId, cols, oldColGroups, colGroups){
			            var store = SHEET_API_HD.store;
			            alert((expand ? 'Expand' : 'Collapse')+(' column '+store.getColName(cols[0])+' to column '+store.getColName(cols[cols.length-1])))
			        },
			        
			         /*
			         * @param {Boolean} state: true for expand, false for collapse
			         * @param {Integer} sheetId: the sheet id
			         * @param {Array} showns: the row index array to show
			         * @param {Array} hiddens: the row index array to hide
			         * @param {Array} oldGroups: the old row group array before this action
			         * @param {Array} groups: the row group array after this action
			         * @param {Array} newLevels: the row group level array after this action
			         */
			        'togglerowgrouplevel': function(state, sheetId, showns, hiddens, oldGroups, newGroups, oldLevels, newLevels){
			            alert((state ? 'Expand' : 'Collapse')+' row group level');
			        },
			        /*
			         * @param {Boolean} state: true for expand, false for collapse
			         * @param {Integer} sheetId: the sheet id
			         * @param {Array} showns: the column index array to show
			         * @param {Array} hiddens: the column index array to hide
			         * @param {Array} oldGroups: the old col group array before this action
			         * @param {Array} groups: the col group array after this action
			         * @param {Array} newLevels: the col group level array after this action                  
			         */
			        'togglecolgrouplevel': function(state, sheetId, showns, hiddens, oldGroups, newGroups, oldLevels, newLevels){
			            alert((state ? 'Expand' : 'Collapse')+' column group level');
			        }
			    });
		    },
		    
		    groupToggleEventListenerJson : {
		    	fileName: 'Group listener',
	            sheets: [
	                {name: 'First', id: 1, color: 'red' }
	            ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" },
	               { sheet:1, name:"rowGroups", ftype:"rowgroup", json: "[{level:3, span:[3,20]},{level:2, span:[7,17]},{level:1, span:[11,15]}]" },
	               { sheet:1, name:"colGroups", ftype:"colgroup", json: "[{level:3, span:[1,9]},{level:2, span:[2,7]},{level:1, span:[3,5]}]" },
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 300}},
		            {sheet: 1, row: 2, col: 2, json: {data: "Add group toggle listener - toggle and see result", fw:"bold", fz:14 }}
			    ]
		    },
		    
		    
		    /**
		     * This is function for feature cell data binding ...
		     * The idea is to call customer defined function and get the result. 
		     * Then display this result into the floating window.
		     */
		    callbackCellDataBindingJson : {
		    	fileName: 'Cell data binding',
	            sheets: [ {name: 'First', id: 1, color: 'red' } ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" },
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 250}},
		            {sheet: 1, row: 2, col: 2, json: {data: "Extra data binding by calling custom defined function", fw:"bold", fz:14 }},
		            {sheet: 1, row: 4, col: 2, json: {data: "1) User defined a callback fn, such as: CUSTOM_BINDING_DATA_FN"}},
			        {sheet: 1, row: 5, col: 2, json: {data: "2) Add onCustomBindingFn: \"CUSTOM_BINDING_DATA_FN\" in the injected cell json data"}},
	            	{sheet: 1, row: 6, col: 2, json: {data: "3) End-user move over the cell, \"CUSTOM_BINDING_DATA_FN\" is called with 3 parameters: cell json object, row index, column index"}},
                    {sheet: 1, row: 7, col: 2, json: {data: "4) CUSTOM_BINDING_DATA_FN return html which will be displayed in the popup window."}},
                    
		            { sheet: 1, row: 9, col: 2, json: {data: "Move mouse over the following name to see more detail information in popup window (binding data from your source)" } }, 
		            { sheet: 1, row: 11, col: 2, json: {data: "Apple Inc.", onCustomBindingFn: "CUSTOM_BINDING_DATA_FN", id:"1000", color: "blue" } }, 
	                { sheet: 1, row: 12, col: 2, json: {data: "Google Inc.", onCustomBindingFn: "CUSTOM_BINDING_DATA_FN", id:"2000", color: "blue" } }, 
	                { sheet: 1, row: 13, col: 2, json: {data: "Taylor Swift", onCustomBindingFn: "CUSTOM_BINDING_DATA_FN", id:"3000", color: "red" } } 
	            ]
		    },
		    
		    /**
		     * This is 2 way data binding ...
		     */
		    callback2wayDataBindingJson : {
		    	fileName: '2 ways data binding',
	            sheets: [ {name: 'First', id: 1, color: 'red' } ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" },
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 250}},
		            {sheet: 1, row: 2, col: 2, json: {data: "2-way data binding by calling custom defined function", fw:"bold", fz:14 }},
		            {sheet: 1, row: 4, col: 2, json: {data: "1) User defined a callback fn, such as: CUSTOM_2WAY_BINDING_DATA_FN"}},
			        {sheet: 1, row: 5, col: 2, json: {data: "2) Add onCustomBindingFn: \"CUSTOM_2WAY_BINDING_DATA_FN\" in the injected cell json data"}},
	            	{sheet: 1, row: 6, col: 2, json: {data: "3) End-user move over the cell, \"CUSTOM_2WAY_BINDING_DATA_FN\" is called with 3 parameters: cell json object, row index, column index"}},
                    {sheet: 1, row: 7, col: 2, json: {data: "4) Custom cell editor is popup for end-user to process actions, and make data communicate between sheet and custom cell editor.."}},
                    
		            { sheet: 1, row: 9, col: 2, json: {data: "Move mouse over the following name to process actions in custom cell editor" } }, 
		            { sheet: 1, row: 11, col: 2, json: {data: "Larry Page", onCustomBindingFn: "CUSTOM_2WAY_BINDING_DATA_FN", id:"1000", color: "blue" } },
		            { sheet: 1, row: 11, col: 3, json: {data: "Google Inc."} }
	            ]
		    },
                  
            callback2wayDataBindingCode : function(){
                var sheet = SHEET_API_HD.sheet, store = SHEET_API_HD.store;
                sheet.on('beforeshowcustombindinginfo', function(obj, sheetId, row, col, region, anchorEl, alignPos){
                    if(1 === sheetId && 11 === row && 2 === col){
                         var cd = sheet.getCellValue(sheetId, row, col);
                         CUSTOM_2WAY_BINDING_DATA_FN(cd, row, col);
                         /*
                          * return false to cancel the original tip showing
                          */
                         return false;
                    }
                }, this);
		    },
		    
		    // complex data binding example
		    dataBindingSubmitJson : {
		    	fileName: 'Cell data binding and submit',
	            sheets: [ {name: 'First', id: 1, color: 'red' } ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" }
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30}}, 
	                {sheet: 1, row: 0, col: 2, json: {width: 235}},
	                {sheet: 1, row: 0, col: 3, json: {width: 250}},
	                {sheet: 1, row: 0, col: 4, json: {width: 250}},
		            {sheet: 1, row: 2, col: 2, json: {data: "Complex example to show data binding from client server and submit data to client server", fw:"bold", fz:14 }},
		            {sheet: 1, row: 4, col: 2, json: {data: "This example to show how to binding data and retrieve data."}},     
		            {sheet: 1, row: 16, col: 0, json: {height: 28}},
		            
		            {sheet: 1, row: 15, col: 2, json: {data: "1) Step 1 - add comments"}},
		            {sheet: 1, row: 15, col: 3, json: {data: "2) Step 2 - binding data from client server"}},
		            {sheet: 1, row: 15, col: 4, json: {data: "3) Step 3 - change data and submit to client server"}},
		            {sheet: 1, row: 16, col: 2, json: { data: "Click here to add comments", it: "button", btnStyle: "color: #FFF; background-color: #900;font-size: 125%;", onBtnClickFn: "BTN_ADD_COMMENT_CALLBACK_FN" }, },
		            {sheet: 1, row: 16, col: 3, json: { data: "Bind server data to comments", it: "button", btnStyle: "color: #FFF; background-color: #900;font-size: 125%;", onBtnClickFn: "BTN_BINDING_DATA_CALLBACK_FN" } },
				    {sheet: 1, row: 16, col: 4, json: { data: "Change and submit to server", it: "button", btnStyle: "color: #FFF; background-color: #900;font-size: 125%;", onBtnClickFn: "BTN_SUBMIT_DATA_CALLBACK_FN" } },
				    {sheet: 1, row: 17, col: 2, json: {data: "1.1) Update cell and add comments"}}, 
				    {sheet: 1, row: 17, col: 3, json: {data: "2.1) Get all cell data with comments"}}, 
				    {sheet: 1, row: 18, col: 3, json: {data: "2.2) Call server to get data"}}, 
				    {sheet: 1, row: 19, col: 3, json: {data: "2.2) Render data to the cell"}}, 
				    {sheet: 1, row: 17, col: 4, json: {data: "3.1) Get all cell data with comments"}},
				    {sheet: 1, row: 18, col: 4, json: {data: "3.2) Call server API to submit data"}}
	            ]
		    },
                  
            getDataFromRangeJson : {
                  fileName: 'Get data from range',
                  sheets: [ {name: 'First', id: 1, color: 'red' }, {name: 'Another', id: 2 } ],
                  floatings: [
                              { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" }
                              ],
                  cells: [
                          {sheet: 1, row: 2, col: 0, json: {height: 30}},
                          {sheet: 1, row: 0, col: 2, json: {width: 235}},
                          {sheet: 1, row: 0, col: 3, json: {width: 250}},
                          {sheet: 1, row: 0, col: 4, json: {width: 250}},
                          {sheet: 1, row: 2, col: 2, json: {data: "Complex example to show data binding from client server and submit data to client server", fw:"bold", fz:14 }},
                          {sheet: 1, row: 4, col: 2, json: {data: "This example to show how to get data from a range such as B2:F4."}},
                          {sheet: 1, row: 16, col: 0, json: {height: 28}},
                          
                          {sheet: 1, row: 16, col: 2, json: { data: "Copy B2:F4 to Another tab", it: "button", btnStyle: "color: #FFF; background-color: teal;font-size: 125%;", onBtnClickFn: "GET_DATA_FROM_RANGE_CALLBACK_FN" }, }
                          ]
                  },
		    
		    dataBindingVariableJson : {
		    	fileName: 'Invoice',
	            sheets: [ {name: 'Invoice', id: 1, color: 'red' } ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,5,3,5]" },
	               { sheet:1, name:"merge2", ftype:"meg", json:"[13,2,13,4]" },
	               { sheet:1, name:"merge3", ftype:"meg", json:"[14,2,14,4]" },
	               { sheet:1, name:"merge4", ftype:"meg", json:"[15,2,15,4]" },
	               { sheet:1, name:"merge5", ftype:"meg", json:"[16,2,16,4]" },
	               { sheet:1, name:"merge6", ftype:"meg", json:"[17,2,17,4]" },
	               { sheet:1, name:"merge7", ftype:"meg", json:"[18,2,18,4]" },
	               { sheet:1, name:"merge8", ftype:"meg", json:"[19,2,19,4]" },
	               { sheet:1, name:"merge9", ftype:"meg", json:"[20,2,20,4]" },
	               { sheet:1, name:"merge10", ftype:"meg", json:"[21,2,21,3]" },
	               { sheet:1, name:"merge11", ftype:"meg", json:"[23,2,23,5]" },
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30}}, 
	                {sheet: 1, row: 23, col: 0, json: {height: 30}}, 
	                {sheet: 1, row: 0, col: 1, json: {width: 30}},
	                {sheet: 1, row: 0, col: 2, json: {width: 250}},
	                {sheet: 1, row: 0, col: 4, json: {width: 150}},
	                {sheet: 1, row: 0, col: 5, json: {width: 150}},	            
		            {sheet: 1, row: 2, col: 2, json: { data: '[Company Name]', vname:'company_name', fz: 14}},
		            {sheet: 1, row: 3, col: 2, json: { data: '[Street Address]', vname:'street_address'}},
		            {sheet: 1, row: 4, col: 2, json: { data: '[City, ST ZIP]', vname:'city_state_zip'}},
		            {sheet: 1, row: 5, col: 2, json: { data: '[Phone]', vname:'phone'}},          
		            {sheet: 1, row: 2, col: 5, json: { data: 'INVOICE', fz: 24, color:'teal', fw:'bold', ta:'right'}},
		            {sheet: 1, row: 4, col: 4, json: { data: 'INVOICE #', ta: 'center'}},
		            {sheet: 1, row: 4, col: 5, json: { data: 'DATE', ta: 'center'}},
		            {sheet: 1, row: 5, col: 4, json: { data: '[Invoice No]', vname:'invoice_no', ta: 'center'}},
		            {sheet: 1, row: 5, col: 5, json: { data: '=today()', cal: true, ta: 'center'}},
		            
		            {sheet: 1, row: 7, col: 2, json: { data: 'BILL TO'}},
		            {sheet: 1, row: 8, col: 2, json: { data: '[Name]', vname:'bill_to.name'}},
		            {sheet: 1, row: 9, col: 2, json: { data: '[Company Name]', vname:'bill_to.company_name'}},
		            {sheet: 1, row: 10, col: 2, json: { data: '[Phone]', vname:'bill_to.phone'}},
		            {sheet: 1, row: 11, col: 2, json: { data: '[Email]', vname:'bill_to.email'}},
		            
		            {sheet: 1, row: 13, col: 2, json: { data: 'DESCRIPTION'}},
		            {sheet: 1, row: 13, col: 5, json: { data: 'AMOUNT'}},
		            {sheet: 1, row: 14, col: 2, json: { data: "[Description]", vname:'list.description'}},
                    {sheet: 1, row: 14, col: 5, json: { data: "[Amount]", vname:'list.amount'}},
		            {sheet: 1, row: 21, col: 4, json: { data: 'TOTAL', ta:'right', fw:'bold'}},
		            {sheet: 1, row: 21, col: 5, json: { data: '=sum(E14:E20)', cal: true, fm: "money|$|2|none"}},
		            {sheet: 1, row: 21, col: 2, json: { data: 'Thank you for your business!'}},
		            {sheet: 1, row: 23, col: 2, json: { data: "Click to bind data with cell variables", it: "button", btnStyle: "color: #FFF; background-color: #900;font-size: 105%;padding:5px 25px;border-radius:19px;-moz-border-radius:19px;-webkit-border-radius:19px;", onBtnClickFn: "BTN_LOAD_INVOICE_DATA_CALLBACK_FN" } }		          
	            ]
		    },
		    
		    /****************************************
		     * below is an example of cell event binding
		     *************************************/
		    cellEventBindingJson : {
		    	fileName: 'Cell data binding',
	            sheets: [ {name: 'First', id: 1, color: 'red' } ],
	            floatings: [
	               { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" },
	            ],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30, hoff:0}},
	                /*
	                 * here we add beforeEdit for row 11 and col B, so any cell in these rows and cols will had this event 
	                 * For now I only add code to monitor beforeEdit action, we can add more if need, such as afterEdit, clickCell
	                 */
	                {sheet: 1, row: 11, col: 0, json: {height: 30, beforeEdit: "_beforeeditcell_"}},	                 
	                {sheet: 1, row: 0, col: 2, json: {width: 250, beforeEdit: "_beforeeditcell_"}},
		            {sheet: 1, row: 2, col: 2, json: {data: "Add customer defined event for some cells, it will be fired before that cell go into editing mode", fw:"bold", fz:14 }},
		            {sheet: 1, row: 4, col: 2, json: {data: "1) User defined an event name, such as: _beforeeditcell_, add _ at begin and end of the event name so it won't repeat the name with other event"}},
			        {sheet: 1, row: 5, col: 2, json: {data: "2) This event '_beforeeditcell_' will be fired by sheet before that cell edit"}},
	            	{sheet: 1, row: 6, col: 2, json: {data: "3) add listener for that event, sheet.on('_beforeeditcell_', function(sheetId, row, col, cellData, sheet){return false;}, this)"}},
                    {sheet: 1, row: 7, col: 2, json: {data: "4) return false in the listener to prevent the original edit action"}},
                    
		            { sheet: 1, row: 11, col: 2, json: {data: "Apple Inc.", color: "chocolate" } }, 
	                { sheet: 1, row: 12, col: 2, json: {data: "Google Inc.", color: "blue" } }, 
	                { sheet: 1, row: 13, col: 2, json: {data: "Taylor Swift", color: "red" } }
	            ]
		    },
		    
		    cellEventBindingCode : function(){
		    	var sheet = SHEET_API_HD.sheet;
		    	/*
		    	 * define the render which will be used to render the cell data
		    	 */
		    	sheet.addCellDataRender({
		    		/*
		    		 * user define the name of his render, and create the function for the render, which will be called when the UI render the cell
		    		 * the params pass to render function will be
		    		 * @param sheetId: the sheet id of current cell
		    		 * @param row: the row index of current cell
		    		 * @param col: the col index of current cell
		    		 * @param data: the cell data object of current cell, notice it's a copied object, no matter what you do with it, it won't affect the cell data saved in database
		    		 * @return: this function need return a string which will render as the content of current cell, this string can be a piece of html
		    		 */
		    		'contactRender': function(sheetId, row, col, data){
		    			/*
		    			 * here we just get the itms and render all elements with css style
		    			 */
		    			var itms = data.itms, arr = [];
		    			if(itms){
		    				itms = Ext.decode(itms);
			    			for(var i = 0, len = itms.length; i < len; i++){
			    				var it = itms[i];
			    				arr.push('<span class="ss-contact" data-qtip="'+it.email+'">'+it.name+'</span>');
			    			}
			    			return arr.join('');
		    			}
		    		}
		    	});
		    	/*
		    	 * here we add listener for the event we defined before, so when user try to edit this cell, this pre-defined event will be fired
		    	 * if we want to create our own way to edit this cell instead of the original way, we need return false in this listener
		    	 * below example defined a customized way to edit the cell, popup a window to let user select the contact they want to add to this cell
		    	 */
		    	sheet.on('_beforeeditcell_', function(sheetId, row, col, cellData, sheet){
		    		var win = Ext.create('Ext.window.Window', {
		    			title: 'Add contact',
		    			width: 300,
		    			height: 300,
		    			modal: true,
		    			layout: 'anchor',
		    			bodyStyle: 'padding:20px;background:white;',
		    			items: [{
		    				xtype: 'checkbox',
		    				boxLabel: 'John Don',		    				
		    				email: 'john@gmail.com',
		    				anchor: '100%'
		    			}, {
		    				xtype: 'checkbox',
		    				boxLabel: 'Adam Lamp',		    				
		    				email: 'adam@gmail.com',
		    				anchor: '100%'
		    			}],
		    			buttons: [{
		    				text: 'OK',
		    				handler: function(){
		    					var arr = [], itms = [];
		    					win.items.each(function(it){
		    						if(it.getValue()){
			    						var name = it.boxLabel, email = it.email; 
			    						arr.push(name);
			    						itms.push({
			    							name: name,
			    							email: email
			    						});
		    						}
		    					})
		    					
		    					if(0 < arr.length){
		    						/*
		    						 * when user check the contact and click OK, we will set these info to the cell
		    						 */
		    						sheet.setCell(sheetId, row, col, {
		    							/*
		    							 * first we create the pure text for data, so it won't cause problem when export
		    							 */
		    							data: arr.join(', '),
		    							/*
		    							 * we add itms to hold the array data, notice the element in this array can be any json obj as you want, 
		    							 * but should not included any function as member of the object
		    							 * @remember we need encode it to json str, so it won't cause problem when copy/paste the cell
		    							 */
		    							itms: Ext.encode(itms),
		    							/*
		    							 * define the render for the cell, it will be called when the cell is rendering on the UI
		    							 */
		    							render: 'contactRender',
		    							va: 'middle'
		    						});
		    						win.close();
		    						sheet.focus(100);
		    					}else{
		    						Ext.Msg.alert('Hint', 'Please at least check one');
		    					}
		    				}
		    			}]
		    		});
		    		win.show();
		    		
		    		/*
		    		 * return false to stop the default edit
		    		 */
		    		return false;
		    	}, this);
		    },
		    
		    /**
		     * This is function for call back during switch tab ..
		     */
		    callbackSheetSwitchJson : {
				fileName: 'onSheetSwitch call back',
	            sheets: [{name: 'Sheet switch', id: 1, color: 'red' }, {name: 'Tab2', id: 2, color: 'blue' }],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" },
	                { sheet:1, name:"merge2", ftype:"meg", json:"[4,2,4,6]" }
				],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30}}, 	
	                {sheet: 1, row: 4, col: 0, json: {height: 120}}, 
			        {sheet: 1, row: 2, col: 2, json: {data: "Switch tab event callback fn", fw:"bold", fz:14 }},
			        {sheet: 1, row: 4, col: 2, json: {data: "var sheet = SHEET_API_HD.sheet;  \n sheet.on({\n \u00a0 \u00a0 \u00a0 scope: this, \n \u00a0 \u00a0 \u00a0 'switchsheet': function(oldSheetId, sheetId) { \n \u00a0 \u00a0 \u00a0 \u00a0 \u00a0 \u00a0 alert('ok'); \n \u00a0 \u00a0 \u00a0 } \n });"}}
	            ]
			},
			
			onSwitchSheet : function(oldSheetId, sheetId) {
        	    alert("Switch from tab: [" + oldSheetId + "] to tab: [" + sheetId + "]");
        	},
		    
		    callbackSheetSwitchCode : function(){
                var sheet = SHEET_API_HD.sheet;
                sheet.un({
                	scope: this,
                	'switchsheet': this.onSwitchSheet
                });
                sheet.on({
                	scope: this,
                	'switchsheet': this.onSwitchSheet
                });
		    },
		    
		    /**
		     * This is function for event listener for copy paste ..
		     */
		    callbackCopyPasteJson : {
				fileName: 'onCopyPaste call back',
	            sheets: [{name: 'Copy Paste', id: 1}],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" },
	                { sheet:1, name:"merge2", ftype:"meg", json:"[3,2,3,6]" },
	                { sheet:1, name:"merge3", ftype:"meg", json:"[4,2,4,6]" }
				],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30}}, 
	                {sheet: 1, row: 3, col: 0, json: {height: 120}},
	                {sheet: 1, row: 4, col: 0, json: {height: 120}}, 
			        {sheet: 1, row: 2, col: 2, json: {data: "Copy paste event callback fn", fw:"bold", fz:14 }},
			        {sheet: 1, row: 3, col: 2, json: {data: "var sheet = SHEET_API_HD.sheet;  \n sheet.on({\n \u00a0 \u00a0 \u00a0 scope: this, \n \u00a0 \u00a0 \u00a0 'copy2clip': function(range, sheet) { \n \u00a0 \u00a0 \u00a0 \u00a0 \u00a0 \u00a0 alert('copy to clip board'); \n \u00a0 \u00a0 \u00a0 } \n });"}},
			        {sheet: 1, row: 4, col: 2, json: {data: "var sheet = SHEET_API_HD.sheet;  \n sheet.on({\n \u00a0 \u00a0 \u00a0 scope: this, \n \u00a0 \u00a0 \u00a0 'paste': function(range, sheet) { \n \u00a0 \u00a0 \u00a0 \u00a0 \u00a0 \u00a0 alert('pasted'); \n \u00a0 \u00a0 \u00a0 } \n });"}}
	            ]
			},
			
			callbackAfterCellChangeJson : {
				fileName: 'aftercellchange call back',
	            sheets: [{name: 'After cell change', id: 1}],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,6]" },
	                { sheet:1, name:"merge2", ftype:"meg", json:"[3,2,3,6]" }
				],
	            cells: [
	                {sheet: 1, row: 2, col: 0, json: {height: 30}}, 
	                {sheet: 1, row: 3, col: 0, json: {height: 120}},
	                {sheet: 1, row: 4, col: 0, json: {height: 120}}, 
			        {sheet: 1, row: 2, col: 2, json: {data: "Aftercellchange event callback fn", fw:"bold", fz:14 }},
			        {sheet: 1, row: 3, col: 2, json: {data: "var store = SHEET_API_HD.store;  \n store.on({\n \u00a0 \u00a0 \u00a0 scope: this, \n \u00a0 \u00a0 \u00a0 'aftercellchange': function(modified, deleted, origin, current, store, sheetId, row, col) { \n \u00a0 \u00a0 \u00a0 \u00a0 \u00a0 \u00a0 alert('Change cell'); \n \u00a0 \u00a0 \u00a0 } \n });"}}			        
	            ]
			},
			
			onCopy2Clip : function(range, sheet) {
				var store = sheet.getStore();
				var coord = range.getCoord(), arr = [];
				for(var i = 0, len = coord.length; i < len; i++){
					var span = coord[i];
					var str = store.getColName(span[2])+span[1]+':'+store.getColName(span[4])+span[3];
					arr.push(str);
				}
        	    alert('Copy at '+ arr.join(', '));
        	},
			
			onPaste : function(range, sheet) {
				var store = sheet.getStore();
				var coord = range.getCoord(), arr = [];
				for(var i = 0, len = coord.length; i < len; i++){
					var span = coord[i];
					var str = store.getColName(span[2])+span[1]+':'+store.getColName(span[4])+span[3];
					arr.push(str);
				}
        	    alert('Paste at '+ arr.join(', '));        	    
        	},
		    
		    callbackCopyPasteCode : function(){
                var sheet = SHEET_API_HD.sheet;
                sheet.un({
                	scope: this,
                	'copy2clip': this.onCopy2Clip
                });
                sheet.on({
                	scope: this,
                	'copy2clip': this.onCopy2Clip
                });
                sheet.un({
                	scope: this,
                	'paste': this.onPaste
                });
                sheet.on({
                	scope: this,
                	'paste': this.onPaste
                });
		    },
		    
		    afterCellChange : function(modified, deleted, origin, current, store, sheetId, row, col){
		    	alert('Change cell '+store.getColName(col)+row);
		    },
		    
		    callbackAfterCellChangeCode : function(){
                var store = SHEET_API_HD.store;
                store.un({
                	scope: this,
                	'aftercellchange': this.afterCellChange
                });
                store.on({
                	scope: this,
                	'aftercellchange': this.afterCellChange
                });                
		    },
		    
		    featureMaxColRowJson : {
				fileName: 'Set Max row/col',
	            sheets: [{name: 'Max', id: 1, color: 'red' }, {name: 'Tab2', id: 2, color: 'blue' }],
	            cells: [
	                {sheet: 1, row: 0, col: 2, json: {width: 200}}, 	
	                {sheet: 1, row: 2, col: 2, json: {data: "Set max row as 30 and max column as 15"}} 
	            ]
			},
		    
		    featureColDataTypeJson : {
		    	fileName: "Employee Directory",
		    	sheets:[{id:1, name:"Main view", actived:true, color:"orange"}],
				floatings: [
			        { sheet:1, name:"colGroups", ftype:"colgroup", json: "[{level:1, span:[2,3]}, {level:1, span:[4,6]}]" },
			    ],
				cells:[
				    { sheet: 1, row: 0, col: 0, json: { height: 20, va: "middle"} },
				    { sheet: 1, row: 0, col: 1, json: { data: "ID", width: 50, dcfg: "{dt:0, io:true, min:0, max:10000, op:0, ignoreBlank: true, titleIcon: \"number\"}", ticon:"number" } },
					{ sheet: 1, row: 0, col: 2, json: { data: "Name", width: 100, ticon:"profile"} },
					{ sheet: 1, row: 0, col: 3, json: { data: "Dept.(Remote)", width: 130, drop: "list", dcfg: "{dt:15, url: \"fakeData/dropdownList\", titleIcon:  \"remoteList\"}", ticon:"remoteList" } },
					{ sheet: 1, row: 0, col: 4, json: { data: "Email", width: 110, dcfg: "{dt:9, ignoreBlank: true}", ticon:"email" } },
					{ sheet: 1, row: 0, col: 5, json: { data: "Phone", width: 100, dcfg: "{dt:8, ignoreBlank: true}", ticon:"phone" } },
					{ sheet: 1, row: 0, col: 6, json: { data: "Gender", width: 80, drop: "list", dcfg: "{dt:13, list: [\"Male\",\"Female\"], ignoreBlank: true}", ticon:"dropdown" } },
					{ sheet: 1, row: 0, col: 7, json: { data: "Birth date", width: 120, drop: "date", fm: "date", dfm: "F d, Y", ticon:"calendar"  } },			
					{ sheet: 1, row: 0, col: 8, json: { data: "Contact picker", width: 170, ticon:"contact", beforeEdit: "_beforeeditcell_" } },
					{ sheet: 1, row: 0, col: 9, json: { data: "Manager?", width: 100, it: "checkbox", itchk: false, ta: "center", ticon:"checkbox" } },
					{ sheet: 1, row: 0, col: 10, json: { data: "Images", width: 130, dcfg: "{dt:7}", ticon:"image" } },
					{ sheet: 1, row: 0, col: 11, json: { data: "Salary", dcfg: "{dt:11, format: \"money|$|2|none|usd|true\"}",  ticon:"money_dollar" } },
					{ sheet: 1, row: 0, col: 12, json: { data: "Percent", dcfg: "{dt:12, format: \"0.00%\"}",  ticon:"percent" } },
					{ sheet: 1, row: 0, col: 13, json: { data: "Notes", dcfg: "{dt:14, titleIcon: \"textLong\"}",  ticon:"textLong" } },
					
					{ sheet: 1, row: 1, col: 1, json: { data: 1 } },
					{ sheet: 1, row: 1, col: 2, json: { data: 'Jerry Marc' } },
					{ sheet: 1, row: 1, col: 3, json: { render:'dropRender', data: 'HR Dept', dropId: 1} },
					{ sheet: 1, row: 1, col: 4, json: { data: 'john.marc@abc.com'} },
					{ sheet: 1, row: 1, col: 5, json: { data: '1 (888) 456-7654'} },
					{ sheet: 1, row: 1, col: 6, json: { data: 'Female'} },
					{ sheet: 1, row: 1, col: 7, json: { data: '1982-01-15', fm: "date", dfm: "F d, Y" } },
					{ sheet: 1, row: 1, col: 8, json: { render:'contactRender', data: "Eva Mat, John Marc", itms: '[{name: "Eva Mat", email: "eva@gmail.com", id: 8}, {name: "John Marc", email: "john@abc.com", id: 9}]' } },
					{ sheet: 1, row: 1, col: 10, json: { render:'attachRender', itms: '[{aid: "rT7KfpHA8cI_", url: "sheetAttach/downloadFile?attachId=rT7KfpHA8cI_", type: "img", name: "blue.jpg"},{aid: "2ZisVQ1-*Lo_", url: "sheetAttach/downloadFile?attachId=2ZisVQ1-*Lo_", type: "img", name: "green.jpg"}]' } },
					{ sheet: 1, row: 1, col: 11, json: { data: 82334.5678 } },
					{ sheet: 1, row: 1, col: 12, json: { data: 0.96 } },
					{ sheet: 1, row: 1, col: 13, json: { data: 'This is notes, it is a long text. Double click to edit it.' } },
					
					{ sheet: 1, row: 2, col: 1, json: { data: 2 } },
					{ sheet: 1, row: 2, col: 2, json: { data: 'Dave Smith' } },
					{ sheet: 1, row: 2, col: 3, json: { render:'dropRender', data: 'Software Dept', dropId: 2} },
					{ sheet: 1, row: 2, col: 4, json: { data: 'dave.smith@abc.com'} },
					{ sheet: 1, row: 2, col: 5, json: { data: '1 (888) 231-7654'} },
					{ sheet: 1, row: 2, col: 6, json: { data: 'Male'} },
					{ sheet: 1, row: 2, col: 7, json: { data: '1980-01-15', fm: "date", dfm: "F d, Y" } },
					{ sheet: 1, row: 2, col: 8, json: { render:'contactRender', data: "Christina Angela, Marina Chris", itms: '[{name: "Christina Angela", email: "christina@gmail.com", id: 4}, {name: "Marina Chris", email: "marina@abc.com", id: 6}]' } },
					{ sheet: 1, row: 2, col: 10, json: { render:'attachRender', itms: '[{aid: "CIBHu3ffG8Q_", url: "sheetAttach/downloadFile?attachId=CIBHu3ffG8Q_", type: "img", name: "admin.png"},{aid: "VcrhEYAyrzA_", url: "sheetAttach/downloadFile?attachId=VcrhEYAyrzA_", type: "img", name: "asset.png"}]' } },
					{ sheet: 1, row: 2, col: 11, json: { data: 81234.5678 } },
					{ sheet: 1, row: 2, col: 12, json: { data: 0.95 } },
					{ sheet: 1, row: 2, col: 13, json: { data: 'This is notes, it is a long text. Double click to edit it.' } },
					
					{ sheet: 1, row: 3, col: 1, json: { data: 3 } },
					{ sheet: 1, row: 3, col: 2, json: { data: 'Kevin Featherstone' } },
					{ sheet: 1, row: 3, col: 3, json: { render:'dropRender', data: 'Software Dept', dropId: 2} },
					{ sheet: 1, row: 3, col: 4, json: { data: 'kevin@abc.com'} },
					{ sheet: 1, row: 3, col: 5, json: { data: '1 (888) 232-7654'} },
					{ sheet: 1, row: 3, col: 6, json: { data: 'Male'} },
					{ sheet: 1, row: 3, col: 7, json: { data: '1990-01-15', fm: "date", dfm: "F d, Y" } },
					{ sheet: 1, row: 3, col: 8, json: { render:'contactRender', data: "Christina Angela, Marina Chris", itms: '[{name: "Christina Angela", email: "christina@gmail.com", id: 4}, {name: "Marina Chris", email: "marina@abc.com", id: 6}]' } },
					{ sheet: 1, row: 3, col: 10, json: { render:'attachRender', itms: '[{aid: "CIBHu3ffG8Q_", url: "sheetAttach/downloadFile?attachId=CIBHu3ffG8Q_", type: "img", name: "admin.png"},{aid: "VcrhEYAyrzA_", url: "sheetAttach/downloadFile?attachId=VcrhEYAyrzA_", type: "img", name: "asset.png"}]' } },
					{ sheet: 1, row: 3, col: 11, json: { data: 81934.5678 } },
					{ sheet: 1, row: 3, col: 12, json: { data: 0.98 } },
					{ sheet: 1, row: 3, col: 13, json: { data: 'This is notes, it is a long text. Double click to edit it.' } }
				]
		    },
		    
		    // this is the survey example json
		    surveyCaseJson : {
		    	fileName: 'EnterpriseSheet Survey',
	            sheets: [{name: 'General', id: 1, color: 'orange' }, 
	                     {name: 'Rate level', id: 2, color: 'green' },
	                     {name: 'Comments', id: 3, color: 'blue' }
	            ],
	            floatings: [
	                { sheet:1, name:"merge1", ftype:"meg", json:"[1,2,1,8]" },
	                { sheet:1, name:"merge2", ftype:"meg", json:"[3,2,3,8]" },
	                { sheet:1, name:"merge2", ftype:"meg", json:"[4,2,4,10]" },
	                { sheet:1, name:"merge3", ftype:"meg", json:"[9,4,9,8]" },
	                { sheet:1, name:"merge4", ftype:"meg", json:"[12,2,12,6]" },
	                { sheet:1, name:"merge5", ftype:"meg", json:"[15,2,15,6]" },
	                { sheet:2, name:"merge20", ftype:"meg", json:"[1,2,1,8]" },
	                { sheet:2, name:"merge21", ftype:"meg", json:"[5,2,5,3]" },
	                { sheet:2, name:"merge22", ftype:"meg", json:"[6,2,6,3]" },
	                { sheet:2, name:"merge23", ftype:"meg", json:"[7,2,7,3]" },
	                { sheet:2, name:"merge24", ftype:"meg", json:"[8,2,8,3]" },
	                { sheet:2, name:"merge25", ftype:"meg", json:"[9,2,9,3]" },
	                { sheet:3, name:"merge30", ftype:"meg", json:"[1,2,1,8]" },
	                { sheet:3, name:"merge31", ftype:"meg", json:"[5,2,12,12]" },
				],
	            cells: [
	                {sheet: 1, row: 1, col: 0, json: {height: 55, bgc: "#000000"}}, 
	                {sheet: 1, row: 3, col: 0, json: {height: 40 }}, 
	                {sheet: 1, row: 4, col: 0, json: {height: 55 }},
	                {sheet: 1, row: 15, col: 0, json: {height: 30 }},
	                {sheet: 1, row: 5, col: 0, json: {height: 25 }},
	                {sheet: 1, row: 1, col: 2, json: {icp:"http://enterprisesheet.com/resources/images/enterpriseLogo.png", imgStyle: "position:absolute;left:0px;top:0px;", data:""}}, 
			        {sheet: 1, row: 3, col: 2, json: {data: "EnterpriseSheet Survey - Form Builder Demo", fz:16, fw:"bold"}},
			        {sheet: 1, row: 4, col: 2, json: {data: "It's our goal, as the customer service team, to assist you quickly and to the best of our ability. To help us improve our service and quality of support for you, please take a few minutes to evaluate our product. Your responses will help us focus on specific areas of improvement as we work to provide the best possible support.", ws: "normal", ww: "break-word"}},
			        
			        { sheet: 1, row: 5, col: 2, json: { data: "1: How did you first learn of EnterpriseSheet?", fz:12 }},
			        { sheet: 1, row: 6, col: 2, json: { data: "Search Engine", it: "radio", itn: "whereLearnES", itchk: true }},
				    { sheet: 1, row: 7, col: 2, json: { data: "Email or Newsletter", it: "radio", itn: "whereLearnES", itchk: false}},
				    { sheet: 1, row: 8, col: 2, json: { data: "Word of Mouth", it: "radio", itn: "whereLearnES", itchk: false}},
				    { sheet: 1, row: 9, col: 2, json: { data: "Others", it: "radio", itn: "whereLearnES", itchk: false}},
				    { sheet: 1, row: 9, col: 4, json: { comment: 'If other, please specify', commentEdit: "hide" } },
				    
				    { sheet: 1, row: 11, col: 0, json: {height: 25 }},
				    { sheet: 1, row: 12, col: 0, json: {height: 20 }},
				    { sheet: 1, row: 11, col: 2, json: { data: "2: Which of the following most accurately describes your occupational title in your organization?", fz:12 }},
				    { sheet: 1, row: 12, col: 2, json: { data: "Staff", drop: Ext.encode({data: ["CEO/Owner/Partner", "Director/VP/Department Header", "Manager", "Team leader", "Developer", "Staff", "Other"]}) }},
				    
				    { sheet: 1, row: 14, col: 0, json: {height: 25 }},
				    { sheet: 1, row: 15, col: 0, json: {height: 20 }},
				    { sheet: 1, row: 14, col: 2, json: { data: "3: What is best description of your organization?", fz:12 }},
				    { sheet: 1, row: 15, col: 2, json: { data: "Software company", drop: Ext.encode({data: ["Software company", "Bank/Financial organization", "Hardware company", "Education organization", "Consultant", "Government", "Other"]}) }},
				    
				    { sheet: 1, row: 17, col: 0, json: {height: 30 }},
				    { sheet: 1, row: 17, col: 10, json: { icp:"http://enterprisesheet.com/sheet/js/EnterpriseSheet/demo/resources/images/next.png", imgStyle: "position:absolute;left:0px;top:0px;width:95%;height:95%;", link: "='Rate level'!$A$2", fm: "link"}},
				    
				    {sheet: 2, row: 0, col: 4, json: {width: 120 }},
	                {sheet: 2, row: 0, col: 5, json: {width: 120 }},
	                {sheet: 2, row: 0, col: 6, json: {width: 120 }},
	                {sheet: 2, row: 0, col: 7, json: {width: 120 }}, 
	                {sheet: 2, row: 1, col: 0, json: {height: 55, bgc: "#000000"}}, 
	                {sheet: 2, row: 4, col: 0, json: {height: 55 }}, 
	                {sheet: 2, row: 5, col: 0, json: {height: 25 }},
	                {sheet: 2, row: 1, col: 2, json: {icp:"http://enterprisesheet.com/resources/images/enterpriseLogo.png", imgStyle: "position:absolute;left:0px;top:0px;", data:""}}, 
			        
			        { sheet: 2, row: 4, col: 0, json: {height: 25 }},			        
				    { sheet: 2, row: 4, col: 2, json: { data: "4: Please rate your level of agreement with the following statements.", fz:12 }},
				    { sheet: 2, row: 5, col: 2, json: { data: "Opinion"}},
				    { sheet: 2, row: 5, col: 4, json: { data: "Strong disagree", ta: "center"}},				    
				    { sheet: 2, row: 5, col: 5, json: { data: "Disagree", ta: "center" }},
				    { sheet: 2, row: 5, col: 6, json: { data: "Agree", ta: "center" }},
                    { sheet: 2, row: 5, col: 7, json: { data: "Strong agree", ta: "center" }},
                    { sheet: 2, row: 6, col: 0, json: { height: 40 }},
                    { sheet: 2, row: 6, col: 2, json: { data: "EnterpriseSheet are priced fairly.", ws: "normal", ww: "break-word"}},
				    { sheet: 2, row: 7, col: 0, json: { height: 40 }},
                    { sheet: 2, row: 7, col: 2, json: { data: "EnterpriseSheet are high quality.", ws: "normal", ww: "break-word"}},
                    { sheet: 2, row: 8, col: 0, json: { height: 45 }},
                    { sheet: 2, row: 8, col: 2, json: { data: "You would recommend EnterpriseSheet to a friend.", ws: "normal", ww: "break-word"}},
                    { sheet: 2, row: 9, col: 0, json: { height: 45 }},
                    { sheet: 2, row: 9, col: 2, json: { data: "Overall, our customer service are satisfied.", ws: "normal", ww: "break-word"}},
                    { sheet: 2, row: 12, col: 0, json: {height: 30 }},
                    { sheet: 2, row: 12, col: 8, json: { icp:"http://enterprisesheet.com/sheet/js/EnterpriseSheet/demo/resources/images/next.png", imgStyle: "position:absolute;left:0px;top:0px;width:95%;height:95%;", link: "='Comments'!$A$2", fm: "link"}},
				    { sheet: 2, row: 12, col: 2, json: { icp:"http://enterprisesheet.com/sheet/js/EnterpriseSheet/demo/resources/images/pre.png", imgStyle: "position:absolute;left:0px;top:0px;width:95%;height:95%;", link: "='General'!$A$2", fm: "link"}},		        
                    
			        { sheet: 2, row: 6, col: 4, json: { data: "1", it: "radio", itn: "priceOK", ta: "center" , va: 'middle', itchk: false }},
				    { sheet: 2, row: 6, col: 5, json: { data: "2", it: "radio", itn: "priceOK", ta: "center" , va: 'middle', itchk: false}},
				    { sheet: 2, row: 6, col: 6, json: { data: "3", it: "radio", itn: "priceOK", ta: "center" , va: 'middle', itchk: true}},
				    { sheet: 2, row: 6, col: 7, json: { data: "4", it: "radio", itn: "priceOK", ta: "center" , va: 'middle', itchk: false}},
				    
				    { sheet: 2, row: 7, col: 4, json: { data: "1", it: "radio", itn: "qualityOK", ta: "center" , va: 'middle', itchk: false }},
				    { sheet: 2, row: 7, col: 5, json: { data: "2", it: "radio", itn: "qualityOK", ta: "center" , va: 'middle', itchk: false}},
				    { sheet: 2, row: 7, col: 6, json: { data: "3", it: "radio", itn: "qualityOK", ta: "center" , va: 'middle', itchk: true}},
				    { sheet: 2, row: 7, col: 7, json: { data: "4", it: "radio", itn: "qualityOK", ta: "center" , va: 'middle', itchk: false}},
				    
				    { sheet: 2, row: 8, col: 4, json: { data: "1", it: "radio", itn: "recommandFriend", ta: "center" , va: 'middle', itchk: false }},
				    { sheet: 2, row: 8, col: 5, json: { data: "2", it: "radio", itn: "recommandFriend", ta: "center" , va: 'middle', itchk: false}},
				    { sheet: 2, row: 8, col: 6, json: { data: "3", it: "radio", itn: "recommandFriend", ta: "center" , va: 'middle', itchk: true}},
				    { sheet: 2, row: 8, col: 7, json: { data: "4", it: "radio", itn: "recommandFriend", ta: "center" , va: 'middle', itchk: false}},
				    
				    { sheet: 2, row: 9, col: 4, json: { data: "1", it: "radio", itn: "customerService", ta: "center" , va: 'middle', itchk: false }},
				    { sheet: 2, row: 9, col: 5, json: { data: "2", it: "radio", itn: "customerService", ta: "center" , va: 'middle', itchk: false}},
				    { sheet: 2, row: 9, col: 6, json: { data: "3", it: "radio", itn: "customerService", ta: "center" , va: 'middle', itchk: true}},
				    { sheet: 2, row: 9, col: 7, json: { data: "4", it: "radio", itn: "customerService", ta: "center" , va: 'middle', itchk: false}},
				    
	                {sheet: 3, row: 1, col: 0, json: {height: 55, bgc: "#000000"}}, 
	                {sheet: 3, row: 4, col: 0, json: {height: 55 }}, 
	                {sheet: 3, row: 5, col: 0, json: {height: 25 }},
	                {sheet: 3, row: 1, col: 2, json: {icp:"http://enterprisesheet.com/resources/images/enterpriseLogo.png", imgStyle: "position:absolute;left:0px;top:0px;", data:""}}, 
	                { sheet: 3, row: 4, col: 0, json: {height: 25 }},			        
				    { sheet: 3, row: 4, col: 2, json: { data: "5: Please enter additional comment/suggestions for EnterpriseSheet. Your comments are really important for us. Thanks", fz:12 }},
				    { sheet: 3, row: 5, col: 2, json: { ws: "normal", ww: "break-word", vname:'customerComments', vnameEdit: "hide"}},
			        { sheet: 3, row: 18, col: 0, json: {height: 30 }},
			        { sheet: 3, row: 15,  col: 2, json: { data: "After submit, you can view survey result in EnterpriseSheet." }},
				    { sheet: 3, row: 18, col: 2, json: { icp:"http://enterprisesheet.com/sheet/js/EnterpriseSheet/demo/resources/images/pre.png", imgStyle: "position:absolute;left:0px;top:0px;width:95%;height:95%;", link: "='Rate level'!$A$2", fm: "link"}},		        
                    { sheet: 3, row: 18, col: 7, json: { data: "Submit", it: "button", btnStyle: "color: #FFF; background-color: #900;font-size: 115%;padding:5px 25px;border-radius:19px;-moz-border-radius:19px;-webkit-border-radius:19px;", onBtnClickFn: "SURVEY_SUBMIT_DATA_CALLBACK_FN" } }	
	            ]
		    },
		    
		    /**
		     * This is a sample json data for dynamicRange
		     */
		    dynamicRangeJson : {
				fileName: 'Dynamic Range',
	            sheets: [{name: 'Dynamic data', id: 1}, {name: 'Static data', id: 2}],
	            /*
	             * here the floating object is not exactly as the document described, I changed a little bit to adopt our current format
	             * notice, the json property need to be a string instead of a json object, also the formula is also put inside the json property.
	             */
	            floatings: [{
	            	"name":"code",
	                "ftype": "dynamicRange",
	                "sheet": 1,
	                "json": Ext.encode({type: "row", span: [1,5,1,5,5], formula: 'getDynamicRange("report1", "code", null)'})	                
	            }, {
	            	"name":"currency",
	                "ftype": "dynamicRange",
	                "sheet": 1,
	                "json": Ext.encode({type: "col", span:[1,2,4,5,4], formula: 'getDynamicRange("report1", "currency", null)'})
	            }],
	            cells: [
	                {sheet: 1, row: 2, col: 2, json: {data: 'CODE', bgc: 'darkblue', color: 'white'}},
	                {sheet: 1, row: 2, col: 3, json: {data: '010', bgc: 'green'}},
	                {sheet: 1, row: 2, col: 4, json: {data: '020', bgc: 'green'}},
	                {sheet: 1, row: 2, col: 5, json: {data: '030', bgc: 'green'}},
	                {sheet: 1, row: 3, col: 1, json: {data: 'Currency', bgc: 'orange'}},
	                {sheet: 1, row: 4, col: 1, json: {data: '010', bgc: 'yellow'}},
	                {sheet: 1, row: 5, col: 1, json: {data: '020', bgc: 'yellow'}},
	                {sheet: 1, row: 7, col: 4, json: {data: '=sum(D4:D6)'}},
	                
	                {sheet: 2, row: 2, col: 2, json: {data: 'CODE', bgc: 'darkblue', color: 'white'}},
	                {sheet: 2, row: 2, col: 3, json: {data: '010', bgc: 'green'}},
	                {sheet: 2, row: 2, col: 4, json: {data: '020', bgc: 'green'}},
	                {sheet: 2, row: 2, col: 5, json: {data: '030', bgc: 'green'}},
	                {sheet: 2, row: 3, col: 1, json: {data: 'Currency', bgc: 'orange'}},
	                {sheet: 2, row: 4, col: 1, json: {data: '010', bgc: 'yellow'}},
	                {sheet: 2, row: 5, col: 1, json: {data: '020', bgc: 'yellow'}},
	                {sheet: 2, row: 7, col: 4, json: {data: '=sum(D4:D6)'}}
	            ]
			},
		});
		
	}
}, function(){
	JSON_DATA = enterpriseSheet.demo.JSONDATA;
});