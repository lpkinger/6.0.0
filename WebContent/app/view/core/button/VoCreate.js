/**
 * 生成凭证按钮
 */	
Ext.define('erp.view.core.button.VoCreate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVoCreateButton',
		iconCls: 'batchdeal',
		cls: 'x-btn-gray',
    	id: 'vocreate',
    	text: $I18N.common.button.erpVoCreateButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 85,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});