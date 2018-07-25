Ext.define('erp.view.crm.marketmgr.marketresearch.MyForm',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.myform',
	id: 'form', 
    frame : true,
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	layout : 'column',
	fieldDefaults : {
	       margin : '2 2 2 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       msgTarget: 'side',
	       blankText : $I18N.common.form.blankText
	},
	initComponent : function(){ 
		this.callParent(arguments);
		formCondition = getUrlParam('formCondition');//从url解析参数
    	formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
    	var type=getUrlParam('type');
    	if(type){
    		var cond=getUrlParam('cond');
    		cond=(cond==null)?'':cond.replace(/IS/g,"=");
    		if(type=='crm'){//市场调研
        		this.getItemsAndButtons({caller: 'Reporttemplate', condition: cond});
        	}else if(type=='ProductTrain'){//产品培训
        		this.getItemsAndButtons({caller: 'PXReporttemplate', condition: cond});
        	}
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
        		if(res.items) {
        			Ext.each(res.items, function(item){
            			if(screen.width >= 1280){//根据屏幕宽度，调整列显示宽度
            				if(item.columnWidth > 0 && item.columnWidth <= 0.34){
            					item.columnWidth = 0.25;
            				} else if(item.columnWidth > 0.34 && item.columnWidth <= 0.67){
            					item.columnWidth = 0.5;
            				}
            			}
            		});
        			me.add(res.items);
        		}
        		if(res.data){
        			me.getForm().setValues(Ext.decode(res.data));
        			me.getForm().getFields().each(function (item, index, length){
        				item.originalValue = item.value;
        			});
        		}
        	}
        });
	}
});