Ext.define('erp.view.ma.copy.Form', {
	extend:'Ext.form.Panel',
	alias:'widget.erpCopyFormPanel',
	id:'form',
    title: '单据复制设置',
	region:'north',
	frame:true,
    layout: 'column',
    autoScroll:true,
    defaultType: 'textfield',
	labeSeparator:':',//默认：
	buttonAlign:'center',
	cls:'u-form-default',
	formCondition:'',
	FormUtil: Ext.create('erp.util.FormUtil'),
   	fieldDefaults:{
   		fieldStyle:'background:#FFFAFA;color:#515151;',
   		focusCls:'x-form-field-cir-focus',
   		labelAlign:'right',
   		msgTarget:'side',
   		blankText:$I18N.common.form.blankText
   	},
   	initComponent : function(){
   		var me = this;
		formCondition = getUrlParam('formCondition');//从url解析参数
		formCondition = (formCondition == null) ? "" : formCondition.replace(/cc_callerIS/g,"");
		me.formCondition = formCondition;
		this.addKeyBoardEvents();
		this.callParent(arguments);
	},
    items: [{
	        fieldLabel: 'CALLER',
	        name: 'cc_caller',
	        id:'cc_caller',
	        xtype:'textfield',
	        readOnly:true,
	        editable:false,
	        allowBlank:true,
	        maxLength:100,
	        maxLengthText:'字段长度不能超过100字符',
	        columnWidth:0.25,
	        cls:'form-field-allowBlank',
	        fieldStyle:'background:#e0e0e0;color:#515151;',
	        allowBlank: true
	    }],
    listeners: {
        'afterrender': function() {
            Ext.getCmp('cc_caller').setValue(formCondition);
        }
    },
    dockedItems:[{
    	xtype:'toolbar',
    	dock:'bottom',
    	defaults:{
    		styple:{
    			marginLeft:'10px'
    		}
    	},
    	items:["->",
    		{
    			xtype:'erpUpdateButton',
    			height:26
    		},{
    			xtype:'erpCloseButton',
    			height:26
    		},"->"]
    }],
	addKeyBoardEvents: function(){
		var me = this;
		Ext.EventManager.addListener(document.body, 'keydown', function(e){
			if(e.altKey && e.ctrlKey) {
				if(e.keyCode == Ext.EventObject.S) {
					var url =  "jsps/ma/detailgrid.jsp?gridCondition=dg_callerIS"+caller;
					me.FormUtil.onAdd('gird' + caller, 'DetailGrid配置维护(' + caller + ')', url);
				}
			}
		});
	}
});