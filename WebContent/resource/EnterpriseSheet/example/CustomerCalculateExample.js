/**
 * Enterprise Spreadsheet Solution
 * Copyright (c) FeyaSoft Inc 2015. All right reserved.
 * http://www.enterpriseSheet.com
 */
Ext.onReady(function(){
    SCONFIG.setupDir('');
    SHEET_API = Ext.create('EnterpriseSheet.api.SheetAPI', {
        openFileByOnlyLoadDataFlag: true
    });
    /*
     * add a new calculate exception
     */
    SHEET_API.addCalculateExceptions({
        'NEW_EXCEPTION_1': function(e){
            return {
                title: 'New Exception 1',
                msg: 'This is a customer defined new exception',
                code: '#EXP1!'
            }
        }
    });
    
    /*
     * add 2 new customized calculates
     */
    SHEET_API.addCalculates({
    	
        'getVersion': {
            /*
             * a simple function return the version of this app
             */
            fn: function(){
                return SCONST['version'];
            },
            hint: ['getVersion', 'getVersion()', '<b>Syntax:</b> getVersion()<br><br>Returns the current version of this EnterpriseSheet App','info']
        },
        
        'customerCal1': {
            /*
             * define the function of this calculate, it's just for demo purpose, this function will checkiing all the calling params if it's a string or the value of the cell it's referenced is a string, then we will get the first letter of every those string to compose a new string
             */
            fn: function(){
                /*
                 * for every calculate function, the scope is an object contains:
                 {
                    me: me, //the calculate class instance itself
                    store: store, //the store instance of the current sheet app
                    sheet: sheetId, //the sheetId of the current cell contains this calculate
                    row: rowIndex, //the row index of the current cell contains this calculate
                    col: colIndex, //the column index of the current cell contains this calculate
                    timestamp: timestamp //a timestamp created when this calculate is called
                 }
                 */
                var me = this.me;
                /*
                 * get calculate instance and use it's checkCoordValid function to check whether the calling params is valid, if not then throw an exception
                 * this function is only valid when the it returns true
                 */
                var result = me.checkCoordValid(arguments);
                if(true !== result) throw {code: 'CAL_INCORRECT_COORD',span: result};
                var arr = [];
                /*
                 * the each function is using to visit every item in the calling params, if it's a range of cells the function will split to single cells to let the callback visit
                 * this function takes 6 params
                    sheetId: the sheetId of the current cell contains this calculate
                    rowIndex: the row index of the current cell contains this calculate
                    colIndex: the column index of the current cell contains this calculate
                    items: the items to iterate, the item element can be a single value or an coord object or an array
                 * callback: the callback function will be invoked for every item and every cell referenced in the coord item
                 * scope: the scope of the callback function
                 */
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
                    /*
                     * throw the NEW_EXCEPTION_1 if there is no string
                     */
                    throw {code: 'NEW_EXCEPTION_1'};
                }

                return arr.join('');
            },
            hint: ['customerCal1', 'customerCal1(str1, str2, ...)', '<b>Syntax:</b> customerCal1()<br><br>Compose a new string by take the first letter of the passed items','info']
        }
    });
    /*
     * create sheet app
     */
    SHEET_API_HD = SHEET_API.createSheetApp({});
    /*
     * render the sheet app in viewport
     */
    Ext.create('Ext.Viewport', {
        layout: 'fit',
        items: [SHEET_API_HD.appCt]
    });
    
    /*
     * create 3 cells with the new calculate we just defined
     */
    var json = {
        fileName:"Basic file",
        sheets:[{id:1,name:"First",actived:true,color:"orange"},{id:2,name:"Second"}],
        cells:[
           { i:1, x:0, y:1, j: "{width: 200}" }, 
           { i:1, x:1, y:1, j: "{data: \"central\"}"}, 
           { i:1, x:2, y:1, j:"{data: \"intelligence\"}"}, 
           { i:1, x:3, y:1, j:"{data: \"agency\"}"},
           { i:1, x:7, y: 1, j: '{data:"customerCal1(A1,A2,A3)"}' }, 
           { i:1, x:7, y: 2, j: '{data:"=customerCal1(A1,A2,A3)", cal:true}' }, 
           { i:1, x:8, y: 1, j: '{data:"customerCal1(A1, A2)" }'},
           { i:1, x:8, y: 2, j: '{data:"=customerCal1(A1, A2)", cal:true}'},
           { i:1, x:10, y:1, j:"{data: \"getVersion()\"}" },
           { i:1, x:10, y:2, j:"{data: \"=getVersion()\", cal:true}" }
        ]
    };
    SHEET_API.loadData(SHEET_API_HD, json);
            
    document.documentElement.style.background = 'none';
    Ext.getBody().setStyle('background-image', 'none');
});