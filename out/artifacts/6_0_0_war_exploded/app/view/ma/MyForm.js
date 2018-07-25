Ext.define('erp.view.ma.MyForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.myform',
    frame : true,
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	style:'background:#f2f2f2;border-bottom:1px solid #bdbdbd',
	padding:'0 0 5 0',
	layout : 'column',
	requires: ['erp.view.core.form.FileField'],
	FormUtil: Ext.create('erp.util.FormUtil'),
	fieldDefaults : {
	       margin : '2 2 2 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},
	initComponent : function(){ 
		this.callParent(arguments);
		formCondition = this.dataId ? ('fo_id=' + this.dataId) : getUrlParam('formCondition');
    	formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
    	var type=getUrlParam('type');
    	if(type&&type=='crm'){
    		var cond=getUrlParam('cond');
    		cond=(cond==null)?'':cond.replace(/IS/g,"=");
    		this.getItemsAndButtons({caller: 'Reporttemplate', condition: cond});
    	}else{
    		this.getItemsAndButtons({caller: this.caller||'Form', condition: formCondition});
    	}
		
	},
	getItemsAndButtons: function(param){
		var me = this, main = parent.Ext.getCmp("content-panel");
		if(main) {
			main.getActiveTab().setLoading(true);
		}
		Ext.Ajax.request({//拿到form的items
        	url : basePath + "common/singleFormItems.action",
        	params: param,
        	method : 'post',
        	callback : function(options,success,response){
        		if(main) {
        			main.getActiveTab().setLoading(false);
        		}
        		var res = new Ext.decode(response.responseText);
        		me.tablename = res.tablename;
        		if(res.items) {
        			var data = res.data ? Ext.decode(res.data) : {};
        			Ext.each(res.items, function(item){
        				delete item.id;
            			if(screen.width >= 1280){//根据屏幕宽度，调整列显示宽度
            				if(item.columnWidth > 0 && item.columnWidth <= 0.34){
            					item.columnWidth = 0.25;
            				} else if(item.columnWidth > 0.34 && item.columnWidth <= 0.67){
            					item.columnWidth = 0.5;
            				}
            			}
            			if(res.data && item.name) {
        					item.value = data[item.name];
        				}
        				item.fieldStyle = 'background:#ffffff;color:#515151;';	
    					if(item.labelAlign&&item.labelAlign!='top'){
							item.labelAlign = 'right';
						}
						if(item.group == '0'){
							item.margin = '7 0 0 0';
						}
            		});
        			/*me.add(res.items);*/
        			me.FormUtil.addItemsForUI(me,res.items,false);
        		}
        	}
        });
	}
});