Ext.define('erp.view.oa.myProcess.timeoutNode.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.erpTimeoutNodeFormPanel',
	id: 'form', 
    region: 'north',
    frame : true,
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '4 2 4 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	tbar: [{
		name: 'query',
		text: $I18N.common.button.erpQueryButton,
		iconCls: 'x-button-icon-query',
    	cls: 'x-btn-gray',
    	handler: function(){
			var grid = Ext.getCmp('grid');
			var form = Ext.getCmp('form');
			var condition = '';
			Ext.each(form.items.items, function(f){
				if(f.logic != null && f.logic != '' && f.value != null && f.value != ''){
					if(contains(f.value, 'BETWEEN', true) && contains(f.value, 'AND', true)){
						if(condition == ''){
							condition += f.logic + " " + f.value;
						} else {
							condition += ' AND ' + f.logic + " " + f.value;
						}
					} else {
						if(condition == ''){
							condition += f.logic + "='" + f.value + "'";
						} else {
							condition += ' AND ' + f.logic + "='" + f.value + "'";
						}
					}
				}
			});
			if(condition != ''){
				url = "oa/myprocess/searchTimeoutJNode.action";
//				url = "oa/myprocess/search2.action";
				form.getGroupDa(condition);
			} else {
				showError('请填写筛选条件');return;
			}
			console.log(condition);
    	}
	}, '-', {
		name: 'summary',
		text: '汇总',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	handler: function(){
//    		var grid = Ext.getCmp('querygrid');
//    		grid.BaseUtil.exportexcel(grid);
    	}
	}, '-', {
		text: $I18N.common.button.erpCloseButton,
		iconCls: 'x-button-icon-close',
    	cls: 'x-btn-gray',
    	handler: function(){
    		var main = parent.Ext.getCmp("content-panel"); 
    		main.getActiveTab().close();
    	}
	}],
	initComponent : function(){ 
//		alert(em_uu + caller);
		var param = {caller: caller, condition: ''};
		// 加一个超时字段;
    	
    	this.FormUtil.getItemsAndButtons(this, 'common/singleFormItems.action', param);
    	var timeout = Ext.create('Ext.form.field.Text',{
		    name: 'name',
	        fieldLabel: '超时大于',
	        allowBlank: true,
	        value:0,
	        readOnly:true
	});
    	var label = Ext.create('Ext.form.Label',{
    		 text: '分钟 ',
    	     margins: '0 0 0 0'
    	})
		this.callParent(arguments);
		this.insert(5,timeout);
		this.add(label);
	},
	getGroupDa : function(condition, page, pageSize){
		var me = Ext.getCmp('grid');
		if(!page){
			page = 1;
		}
		if(!pageSize){
			pageSize = 15;
		}
		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + url,
        	params: {
        		page: page,
        		pageSize: pageSize,
        		condition: condition
        	},
        	method : 'post',
        	async: false,
        	callback : function(options, success, response){
//        		console.log(response);
        		parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		if(!res.jprocesslist){
        			return;
        		} else {
        			console.log(res.jprocesslist);
        			dataCount = res.count;
        			me.store.loadData(res.jprocesslist);
        		}
        	}
        });
	}
});