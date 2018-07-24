/**
 * 保存按钮
 * 适用于单据新增页面的保存，
 * 使用时，只需传递一个提交后台的saveUrl即可
 * @author yingp
 * @date 2012-08-03 10:45:49
 */	
Ext.define('erp.view.core.button.TurnProductSample',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnProductSampleButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'TurnProductSample',
    	tooltip: '转录打样申请单',
    	id: 'erpTurnProductSampleButton',
    	text: $I18N.common.button.erpTurnProductSampleButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 120,
		handler: function(){
		}
	});