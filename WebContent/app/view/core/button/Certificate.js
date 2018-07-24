/**
 * 凭证制作按钮
 */	
Ext.define('erp.view.core.button.Certificate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCertificateButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpCertificateButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});