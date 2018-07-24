/**
 * 更新线别
 */	
Ext.define('erp.view.core.button.UpdateTeamcode',{
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateTeamcodeButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	tooltip: '更新线别',
    	id: 'erpUpdateTeamcodeButton',
        formBind: true,
    	text: $I18N.common.button.erpUpdateTeamcodeButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});