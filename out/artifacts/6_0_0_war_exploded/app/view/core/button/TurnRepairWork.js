/**
 * 转维修单
 * @author yingp
 * @date 2012-08-03 10:45:49
 */	
Ext.define('erp.view.core.button.TurnRepairWork',{
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnRepairWorkButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	tooltip: '转维修单',
    	id: 'erpTurnRepairWorkButton',
        formBind: true,
    	text: $I18N.common.button.erpTurnRepairWorkButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 120
	});