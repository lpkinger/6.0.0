/**
 * 修改按钮
 */	
Ext.define('erp.view.core.button.BeforeUpdate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBeforeUpdateButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpBeforeUpdateButton,
    	id:'beforeupdatebutton',
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
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