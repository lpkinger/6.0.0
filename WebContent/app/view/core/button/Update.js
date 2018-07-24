/**
 * 修改按钮
 */	
Ext.define('erp.view.core.button.Update',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpUpdateButton',
		iconCls: 'x-button-icon-modify',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpUpdateButton,
    	id:'updatebutton',
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