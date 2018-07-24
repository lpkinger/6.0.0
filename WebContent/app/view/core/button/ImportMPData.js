/**
 * SMT上料导入备料单数据
 */
Ext.define('erp.view.core.button.ImportMPData',{ 
		extend: 'Ext.Button', 
		id :'importMPDataBtn',
		alias: 'widget.erpImportMPDataButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpImportMPDataButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 130,  
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});