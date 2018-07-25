/**
 * 删除按钮
 */	
Ext.define('erp.view.core.button.Delete',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDeleteButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id:'deletebutton',
    	text: $I18N.common.button.erpDeleteButton,
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