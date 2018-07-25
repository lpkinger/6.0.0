/**
 * 抓取标准单价（采购单）
 */
Ext.define('erp.view.core.button.StandardPrice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpStandardPriceButton',
		param: [],
		id: 'erpStandardPriceButton',
		text: $I18N.common.button.erpStandardPriceButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 130,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});