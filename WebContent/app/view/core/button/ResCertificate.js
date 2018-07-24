/**
 * 凭证取消按钮
 */	
Ext.define('erp.view.core.button.ResCertificate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResCertificateButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpResCertificateButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});