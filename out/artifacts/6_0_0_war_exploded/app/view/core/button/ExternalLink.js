/**
 * 外部链接按钮
 * 用于供应商资料单据审核后，点击跳转外部链接地址。
 * @author chenw
 * @date 2018-04-24 11:24:03
 */	
Ext.define('erp.view.core.button.ExternalLink',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpExternalLinkButton',
		param: [],
		id: 'externalLink',
		text: $I18N.common.button.erpExternalLinkButton,
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 130,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});