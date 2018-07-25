Ext.define('erp.view.excel.ExcelPanel',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpExcelFormPanel',
	id: 'excelform', 
    region: 'north',
    frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '2 2 2 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       focusCls: 'x-form-field-cir',//fieldCls
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	params: null,
	initComponent : function(){ 
		var id= getUrlParam('id');
		id=3008;
		this.params={id:id};
    	this.getItemsAndButtons(this, 'excel/getTemplateCondition.action', this.params );//从后台拿到formpanel的items
		this.callParent(arguments);
	},
	getItemsAndButtons:function(form,url,param){
		var me = this;
		Ext.Ajax.request({//拿到form的items
        	url : basePath + url,
        	params: param,
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo != null){
        			showError(res.exceptionInfo);return;
        		}
        		var items=res.items;
        		form.add(items);
        	/*	var buttons = new Array();
        		buttons.push('->');
        		var o = {};
    			o.xtype = 'erpSaveButton';
    			buttons.push(o);
    			o.xtype='erpCloseButton';
    			buttons.push(o);
    			buttons.push('->');
    			form.addDocked({
        			xtype: 'toolbar',
        	        dock: 'bottom',
        			defaults: {
        				style: {
        					marginLeft: '14px'
        				}
        			},
        	        items: buttons//12个加进去
        		});*/
        		if(res.title && res.title != ''){
        			form.setTitle(res.title);
        		}
        		me.focusFirst(form);
        	}
        });	
	},
	focusFirst: function(form){
		var bool = true;
		Ext.each(form.items.items, function(){
			if(bool && this.hidden == false && this.readOnly == false && this.editable == true){
				this.focus(false, 200);
				bool = false;
			}
		});
	},
});