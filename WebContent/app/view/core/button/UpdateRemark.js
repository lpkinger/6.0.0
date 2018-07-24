/**
 * 更新备注
 */	
Ext.define('erp.view.core.button.UpdateRemark',{
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateRemarkButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	tooltip: '更新备注',
    	id: 'erpUpdateRemarkButton',
        formBind: true,
    	text: $I18N.common.button.erpUpdateRemarkButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 120
	});