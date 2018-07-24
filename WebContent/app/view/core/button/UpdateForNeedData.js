/**
 * 修改按钮
 */	
Ext.define('erp.view.core.button.UpdateForNeedData',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateNeedDataButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: '更新需求日期',
    	id:'updatebutton',
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
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