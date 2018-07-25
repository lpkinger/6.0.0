Ext.QuickTips.init();
Ext.define('erp.controller.ma.group.BaseData', {
    extend: 'Ext.app.Controller',
    views: ['ma.group.BaseData'],
    requires: ['erp.util.BaseUtil'],
    refs : [ {
		ref : 'form',
		selector : 'form'
	}],
    init:function(){
    	this.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({
    		'form': {
    			afterrender: function() {
    				this.getBaseDataSet();
    			}
    		},
    		'#confirm': {
    			click: function() {
    				this.saveBaseDataSet();
    			}
    		},
    		'#close': {
    			click: function() {
    				this.BaseUtil.getActiveTab().close();
    			}
    		},
    		'#selectall': {
    			change: function(f) {
    				var form = this.getForm();
    				form.getForm().getFields().each(function(a){
    					if(a.id != f.id) {
    						a.setValue(f.value);
    					}
    				});
    			}
    		}
    	});
    },
    getBaseDataSet: function() {
    	var form = this.getForm(), tab = this.BaseUtil.getActiveTab();
    	tab.setLoading(true);
    	Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		params: {
	   			caller: 'BaseDataSet',
	   			fields: 'bds_caller,bds_desc,bds_editable',
	   			condition: '1=1'
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			tab.setLoading(false);
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			}
    			if(r.success){
    				var data = Ext.decode(r.data), items = new Array();
    				for(var i in data) {
    					var d = data[i];
    					var o = {boxLabel: d.BDS_DESC, checked: d.BDS_EDITABLE != 0, caller: d.BDS_CALLER};
    					items.push(o);
    				}
    				form.add(items);
	   			}
	   		}
		});
    },
    saveBaseDataSet: function() {
    	var form = this.getForm(),
	    	items = form.query('checkbox[checked=true]'),
			data = new Array();
		Ext.each(items, function(item){
			if (item.caller)
				data.push("'" + item.caller + "'");
		});
		Ext.Ajax.request({
			url: basePath + 'ma/group/updateBaseDataSet.action?caller=' + caller,
			params: {
				data: data.join(',')
			},
			callback: function(opt, s, res) {
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			}
    			if(r.success){
    				alert('设置成功!');
    			}
			}
		});
    }
});