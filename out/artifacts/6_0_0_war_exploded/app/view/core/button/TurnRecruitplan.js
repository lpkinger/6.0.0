/**
 * 批量删除按钮
 */	
Ext.define('erp.view.core.button.TurnRecruitplan',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnRecruitplanButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'TurnRecruitplan',
    	tooltip: '转招聘计划',
    	id: 'erpTurnRecruitplanButton',
    	text: $I18N.common.button.erpTurnRecruitplanButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 120,
		handler: function(){
		}
	});