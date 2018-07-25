/*
 * 库存运算及上架，查询上架信息按钮
 */
Ext.define('erp.view.core.button.DullStockUp',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDullStockUpButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'dullstockupbutton',
    	text: $I18N.common.button.erpDullStockUpButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners: {
			afterrender: function(btn) {
				var form = btn.ownerCt.ownerCt;
				if(form && form.readOnly) {
					btn.hide();
				}
			}
		}
	});