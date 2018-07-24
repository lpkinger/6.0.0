/*
 * This is an example for cross file reference
 */
//create 2 sheet instances
var sheet = Ext.create('EnterpriseSheet.pure.sheet.Basic', {});
var sheet2 = Ext.create('EnterpriseSheet.pure.sheet.Basic', {});
var store = sheet.getStore();
var store2 = sheet2.getStore();

var json2 = {
	fileName: 'CrossFile',
	sheets: [
	    {name: 'Sheet1', id: 1 }
	],	    
	cells: [
	    {sheet: 1, row: 1, col: 1, json: {data: 'I am from other file'}},
	    {sheet: 1, row: 2, col: 1, json: {data: 22}},
	    {sheet: 1, row: 3, col: 1, json: {data: 49}},
	    //this cell is a formula refer to the other file
	    {sheet: 1, row: 4, col: 1, json: {data: '=sum([CurFile]List!B4:B6)'}}
	]
};
/*
 * load file for these 2 sheets
 */
store2.loadJsonFile(json2);

var json = {
	fileName: 'CurFile',
    sheets: [
        {name: 'List', id: 1, color: 'red' }
    ],
    floatings: [
        { sheet:1, name:"merge1", ftype:"meg", json:"[2,2,2,8]" },
		{sheet:1, name:"validation1",ftype:"cdt", json: "{name:\"vd\",rng:[{span:[1,4,2,5,4],type:1}],opt:{dt:3,op:0,list:[\"Apple\",\"Orange\",\"Banana\",\"Kiwi\"],hint:\"Fruit\",allow:true},id:\"vd-1\"}"}
	],
    cells: [        
	    {sheet: 1, row: 2, col: 2, json: {data: "Add Date validation to the table", fw:"bold", fz:14 }}, 	        
	    {sheet: 1, row: 4, col: 2, json: {data: 1}},
	    {sheet: 1, row: 4, col: 3, json: {data: 11}},
	    {sheet: 1, row: 4, col: 4, json: {data: 111}},	    
	    {sheet: 1, row: 6, col: 1, json: {data:"=TODAY()"}},
	    {sheet: 1, row: 6, col: 2, json: {data:"=A6*A6"}},
	    //this cell is a formula refer to the other file
	    {sheet: 1, row: 6, col: 3, json: {data:"=sum([CrossFile]Sheet1!A:A)"}}
	]
};
store.loadJsonFile(json);

/*
 * add reference to each other
 */
sheet.addCrossFileReference(store2.getFileName(), sheet2);
sheet2.addCrossFileReference(store.getFileName(), sheet);
/*
 * get the result after add reference
 */
console.log('[CrossFile]Sheet1!A4'+store2.getCell(1, 4, 1).data+' = '+sheet2.getCellValue(1, 4, 1).data);
console.log('[CurFile]Sheet1!C6'+store.getCell(1, 6, 3).data+' = '+sheet.getCellValue(1, 6, 3).data);

console.log('example of getCrossFileRef function');
console.log(sheet.getCrossFileRef('CrossFile', 'Sheet1!A1'));
console.log(sheet.getCrossFileRef('', '[CrossFile]Sheet1!A1'));

//when update a cell in sheet, it will cause the cell in sheet2 change
console.log('change [CurFile]Sheet1!B4 to 201')
sheet.updateCells([{
	sheet: 1, 
	row: 4,
	col: 2,
	json: {
		data: 201
	}
}], function(){	
	//store2.insertColumn(1, 1, 1)
	//sheet2.sortSpan([1, 1, 2, 3, 2], undefined, 'asc');
	//sheet2.mergeCell([1, 1, 1, 1, 2]);
	
	//sheet2.unmergeCell([1, 1, 1, 2, 2]);
	console.log('The related cells in both files are changed');
	console.log('[CrossFile]Sheet1!A4 = '+sheet2.getCellValue(1, 4, 1).data);
	//console.log(sheet.getCellValue(1, 6, 1).data);
	//console.log(store.getCell(1, 6, 3).data);
	//console.log(sheet.getCellValue(1, 6, 3).arg);
	console.log('[CurFile]Sheet1!C6 = '+sheet.getCellValue(1, 6, 3).data);	
	//sheet2.unmergeCell([1, 1, 1, 1, 2]);
	//console.log(sheet.getCellValue(1, 6, 3).data);
});