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
Ext.define('EnterpriseSheet.Config', {		
	
	constructor : function() {
			
        this.setupDir('');
        
		Ext.apply(this, {
		    // please select one of the following currency code:
			//    'usd', 'rmb', 'eur', 'ars' , 'aud', 'brl', 'cad', 'clp', 'cop', 'dkk', 'hkd', 'isk', 'inr', 'idr', 'ils', 'jpy'		
			//	  'won', 'mxn', 'myr', 'nzd', 'nok', 'pln', 'rub', 'sar', 'sgd', 'zar', 'sek', 'chf', 'twd', 'try', 'gbp', 'afn'
			//    'bob', 'bgn', 'egp', 'ltl', 'vnd', 'uah', 'irr', 'huf', 'cup', 'ron', 'jmd', 'kzt', 'lbp', 'thb', 'ngn', 'zwd'
			//    'all', ''
			default_currency: 'usd',
			
			// Please select one of the following items:
			//     en_US, zh_CN
			default_locale: 'en_US',

			// Please contact us for add more date, time format
            en_US_moreDateTimeFm: ['M d, Y, H:i:s', 'M d, Y, H:i', 'M d, Y, g:i:s A', 'l, M d, Y, g:i:s A', 'Y/m/d H:i', 'Y/m/d H:i:s'],
            zh_CN_moreDateTimeFm: ['Y\u5E74m\u6708j\u65E5', 'y\u5E74m\u6708j\u65E5', 'm\u6708j\u65E5', 'Y\u5E74m\u6708j\u65E5 G\u70B9i\u5206', 'Y\u5E74m\u6708j\u65E5 H\u70B9i\u5206', 'Y\u5E74m\u6708j\u65E5 G\u70B9i\u5206s\u79D2',
                                     'M d, Y, H:i:s', 'M d, Y, H:i', 'M d, Y, g:i:s A', 'l, M d, Y, g:i:s A', 'Y/m/d H:i', 'Y/m/d H:i:s'],
                                     
            // set sheet tab bar position: top OR bottom
            sheet_tab_bar_position: 'top',
            
            // hide or show language menu
            language_menu_hide: false,
                        
            // disable file menu if set as false
            file_menu_hide: false,
            
            // disable import / export
            enableExport : true,
            enableImport : true,
            
            // Set the contextmenu items - here list the default list, please update the list as your needed.
            // Default are those actions:
            //        ["freeze", "split", "-", "insert", "insertCopied", "delete", "clean", "-", "hideRow", "showRow", "rowHeight", "hideColumn", "showColumn", "columnWidth", "-", "insertComment", "markRange", "insertVariable", "-", "addGroup", "cancelGroup", "hyperlink", "validate"],
            // If you want add your customized item, follow this example:
            //        {text: 'My customized item', handler: function() { alert("ok"); } }
            contextmenu_items : [ // {text: 'My customized item',handler: function() { alert("ok"); } }, 
                "freeze", "split", "-", "cut", "copy", "paste", "-", "insert", "insertCopied", "delete", "clean", "-",
                "hideRow", "showRow", "rowHeight", "hideColumn", "showColumn", "columnWidth", "-",
                "insertComment", "markRange", "insertVariable", "-", "addGroup", "cancelGroup",
                "hyperlink", "validate", "setHeaderTitle", "hideTitle", "showTitle"],
            
            // this is used for set arrow menu
            // Default are those actions:
            //     ['sortAsc', 'sortDesc', 'filter', 'columnWidth', 'rowHeight', '-', 'hide', 'delete']
            arrowmenu_items : ['config', "colTitle", "icon", "hideTitle", "showTitle", '-', 'sortAsc', 'sortDesc', 'filter', 'columnWidth', 'rowHeight', '-', 'hide', 'delete'],

            fontFamilyDataCN : [	        	
    			['\u5B8B\u4F53', '<font face="STXihei">\u5B8B\u4F53</font>'],
    			['\u6977\u4F53', '<font face="STSong">\u6977\u4F53</font>'],
    			['\u4EFF\u5B8B\u4F53', '<font face="STKaiti">\u4EFF\u5B8B\u4F53</font>'],
    			['\u65B0\u5B8B\u4F53', '<font face="STHeiti">\u65B0\u5B8B\u4F53</font>'],
    			['\u9ED1\u4F53', '<font face="Hiragino Sans GB">\u9ED1\u4F53</font>'],
    			['Arial', '<font face="arial">Arial</font>'],
    			['Antiqua', '<font face="antiqua">Antiqua</font>'],
    			['Calibri', '<font face="calibri">Calibri</font>'],
    			['Comic Sans MS', '<font face="Comic Sans MS">Comic Sans MS</font>'],
    			['Courier New', '<font face="courier">Courier New</font>'],
    			['Garamond', '<font face="Garamond">Garamond</font>'],
    			['Georgia', '<font face="Georgia">Georgia</font>'],
    			['Helvetica', '<font face="helvetica">Helvetica</font>'],
    			['Lucida Console', '<font face="Lucida Console">Lucida Console</font>'],
    			['MS Serif', '<font face="MS Serif">MS Serif</font>'],
    			['Monospace', '<font face="Monospace">Monospace</font>'],
    			['Tahoma', '<font face="tahoma">Tahoma</font>'],
    			['Times New Roman', '<font face="times">Times New Roman</font>'],
    			['Verdana', '<font face="verdana">Verdana</font>']
    		],
    			
    		fontFamilyDataEN : [	        	
    		    ['Arial', '<font face="arial">Arial</font>'],
    		    ['Antiqua', '<font face="antiqua">Antiqua</font>'],
    		    ['Calibri', '<font face="calibri">Calibri</font>'],
    		    ['Comic Sans MS', '<font face="Comic Sans MS">Comic Sans MS</font>'],
    		    ['Courier New', '<font face="courier">Courier New</font>'],
    		    ['Garamond', '<font face="Garamond">Garamond</font>'],
    		    ['Georgia', '<font face="Georgia">Georgia</font>'],
    		    ['Helvetica', '<font face="helvetica">Helvetica</font>'],
    		    ['Lucida Console', '<font face="Lucida Console">Lucida Console</font>'],
    		    ['MS Serif', '<font face="MS Serif">MS Serif</font>'],
    		    ['Monospace', '<font face="Monospace">Monospace</font>'],
    		    ['Tahoma', '<font face="tahoma">Tahoma</font>'],
    		    ['Times New Roman', '<font face="times">Times New Roman</font>'],
    		    ['Verdana', '<font face="verdana">Verdana</font>'],
    		    ['\u5B8B\u4F53', '<font face="STXihei">\u5B8B\u4F53</font>'],
    		    ['\u6977\u4F53', '<font face="STSong">\u6977\u4F53</font>'],
    		    ['\u4EFF\u5B8B\u4F53', '<font face="STKaiti">\u4EFF\u5B8B\u4F53</font>'],
    		    ['\u65B0\u5B8B\u4F53', '<font face="STHeiti">\u65B0\u5B8B\u4F53</font>'],
    		    ['\u9ED1\u4F53', '<font face="Hiragino Sans GB">\u9ED1\u4F53</font>']
    		],
            // this flag is set to see whether it is standalone version - only js code
            js_standalone: false,
            
            /*
             * can be one of below value, if empty or '' then means default
             * default: means paste everything from the copied cells, included data and style
             * data: only paste the data from the copied cells
             * style: only paste the style from the copied cells
             * reverse: paste the copied cells in the reverse way
             */
            DEFAULT_PASTE_TYPE: '',
            
            // during popup sort 
            DISABLE_SORT_CURRENT_RANGE: false,
            DISABLE_MOVE_CURRENT_CELLS: false,
	    	FILTER_SAVE_DISABLE: false,            
            // this is the check whether scroll bar always show ...
            SCROLLER_ALWAYS_DISABLE: true,

            hideOverflowInCell: false,
            
            SKIP_HIDDEN_ROW_AUTOFILL: false
		})
	},
    
    setupDir : function(dir){
    	dir=basePath;
        Ext.apply(this, {
            baseDir: dir,
                  
            IMAGES_PATH : dir+'resource/EnterpriseSheet/resources/images',
                     
            ICONS_PATH : dir+'resource/EnterpriseSheet/resources/images/icons',
                     
            TITLE_ICONS_PATH : dir+'resource/EnterpriseSheet/resources/images/icons/title',
                     
            CONDITION_ICONS_PATH: dir+'resource/EnterpriseSheet/resources/images/icons/conditional_icons',
     
            urls: {
            	//2018 3.18 zdw
            	'documentInfo': dir+'Excel/common/getExcelInfo.action',
            	
                'list': dir+'document/list',
                'listOpen': dir+'document/list',
		        'listSheet': dir+'document/list?onlyTpl=true',
		        
		        //2018 3.18 zdw
                'changeFileName': dir+'Excel/common/changeFileName.action',
                
                'changeFileColor': dir+'document/updateColor',
                'changeFileStared': dir+'document/changeFileStared',
                'createFile': dir+'document/createFile',
                'updateLang': dir + 'userSetting/updateLang',
                     
                'findCells': dir+'sheet/findCells',
                'findCells2': dir+'sheet/findCells2',                  
                'loadCells': dir+'sheet/loadCells',
                'loadSheetInfo': dir+'sheet/loadSheetInfo',
                //2018 3.16 zdw
                'loadSheetInfo2': dir+'Excel/common/loadExcelInfo.action',
                
                'loadSheet': dir+'sheet/loadSheet',
                'loadSheet2': dir+'sheet/loadSheet2',
                'loadSheet3': dir+'sheet/loadSheet3',
                'loadSheet4': dir+'sheet/loadSheet4',
                
                
                //2018 3.17 zdw
                'loadSheet5': dir+'Excel/common/loadSheet5.action',
                'loadActivedSheetOfFile': dir+'sheet/loadActivedSheetOfFile',
                'loadActivedSheetOfFile2': dir+'sheet/loadActivedSheetOfFile2',
                'loadActivedSheetOfFile3': dir+'sheet/loadActivedSheetOfFile3',
                'loadSheetsOfFile': dir+'sheet/loadSheetsOfFile',
                'loadSheetsOfFile2': dir+'sheet/loadSheetsOfFile2',
                'loadSheetsOfFile3': dir+'sheet/loadSheetsOfFile3',
                'loadRange': dir+'sheet/loadRange',
                'loadRange2': dir+'sheet/loadRange2',
                
                
                //2018 4.10 zdw
                'loadRange3': dir+'Excel/common/loadRange3.action',
                
                
                
                
                'loadCellOnDemand': dir+'sheet/loadCellOnDemand',
                'loadCellOnDemand2': dir+'sheet/loadCellOnDemand2',
                
                //2018 4.10 zdw
                'loadCellOnDemand3': dir+'Excel/common/loadCellOnDemand3.action',
                
                
                'loadCellOnDemand4': dir+'sheet/loadCellOnDemand4',
                'loadCellOnDemand5': dir+'sheet/loadCellOnDemand5',
                //2018 3.17 zdw
                'loadElementOnDemand': dir+'Excel/common/loadElementOnDemand.action',
                'loadCalCellOnDemand': dir+'sheet/loadCalCellOnDemand',
                'loadFile': dir+'sheet/loadFile',
                'copyFromTpl': dir+'sheet/copyFromTpl',
                'importExcelUpload': dir+'sheet/uploadFile',
                'exportExcel': dir+'sheet/export',
                'uploadImage': dir+'sheet/uploadImage',
                //2018 3.17 zdw     
                'update': dir+'Excel/cell/updateBatchCells.action',
                //2018 3.18 zdw
                'renameSheet': dir+'Excel/sheet/renameSheet.action',
                //2018 3.18 zdw
                'createSheet': dir+'Excel/sheet/createSheet.action',
                //2018 3.18 zdw
                'deleteSheet': dir+'Excel/sheet/deleteSheet.action',
                //2018 3.27 zdw
                'changeSheetOrder': dir+'Excel/sheet/changeSheetOrder.action',
                //2018 3.27 zdw
                'copySheet': dir+'Excel/sheet/copySheet.action',               
                
                
                'changeSheetColor': dir+'sheetTab/changeSheetColor',
                'changeSheetWidth': dir+'sheetTab/changeSheetWidth',
                
                'updateSheetTab': dir+'sheetTab/update',                  
                     
                'listCustom': dir+'sheetCustom/list',
                'addCustom': dir+'sheetCustom/create',
                'deleteCustom': dir+'sheetCustom/delete',
                'listDataset': dir+'sheetDropdown/list',
                'createDataset': dir+'sheetDropdown/createUpdate',
                'loadDataset': dir+'sheetDropdown/load',
                'deleteDataset': dir+'sheetDropdown/delete',
                'saveJsonFile': dir+'sheetapi/saveJsonFile',
                'saveFileAs': dir+'sheet/saveFileAs',
                'createServerErrorReport': dir+'forumPosting/createServerErrorReport',
                'uploadFile': dir+'sheetAttach/uploadFile',
                'downloadFile': dir+'sheetAttach/downloadFile',
                'deleteAttach': dir+'sheetAttach/deleteFile',
                'loadRangeStyle': dir+'sheet/loadRangeStyle',
                'updateExtraInfo': dir+'sheetTab/updateExtraInfo'                                
            },
            BLANK_PHOTO: dir+'resource/EnterpriseSheet/resources/images/photo_.png',
            ATTACH_ICON16: dir+'resource/EnterpriseSheet/resources/images/icons/attach1.png',
            ATTACH_ICON32: dir+'resource/EnterpriseSheet/resources/images/icons/32px/attach.png'
        });
    }
}, function(){
	SCONFIG = Ext.create('EnterpriseSheet.Config');	
});
