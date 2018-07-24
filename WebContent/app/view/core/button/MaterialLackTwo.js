/**
 * 缺料二维查看
 */	
Ext.define('erp.view.core.button.MaterialLackTwo',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpMaterialLackTwoButton',
		iconCls: 'x-button-icon-scan',
    	cls: 'x-btn-gray',
    	text: '缺料二维查看',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
        id:'MaterialLackTwo',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});