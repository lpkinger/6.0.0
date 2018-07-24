Ext.define('erp.view.oa.flow.button.SubmitButton',{ 
		extend: 'Ext.Button', 
		alias: 'widget.SubmitButton',
		text: '提交',
    	cls: 'x-btn-gray',
    	margin:'0 5 0 0',
    	_url:'oa/flow/commit.action',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler:function(btn){
			var form = btn.ownerCt.ownerCt;
			form.beforeSave(form,btn._url);
		}
	});