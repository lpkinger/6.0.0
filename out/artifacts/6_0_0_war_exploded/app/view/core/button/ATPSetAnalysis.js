/**
 * ATP运算套料分析
 */	
Ext.define('erp.view.core.button.ATPSetAnalysis',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpATPSetAnalysisButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'atpsetanalysisbutton',
    	text: $I18N.common.button.erpATPSetAnalysisButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});