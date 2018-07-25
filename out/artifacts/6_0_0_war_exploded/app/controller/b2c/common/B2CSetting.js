Ext.QuickTips.init();
Ext.define('erp.controller.b2c.common.B2CSetting', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'b2c.common.B2CSetting','core.button.Add','core.button.Save','core.trigger.DbfindTrigger'
    	],
    init:function(){
        var me=this;
    	this.control({  
    		'#form':{
    			beforerender:function(f){
    				me.getB2CSetting();
    			}
    		},
    		'#startB2C':{//启用
    			click:function(btn){
    				me.startB2C();
    			}
    		},
    		'#save':{
    			click:function(btn){
    				me.save();
    			}
    		},
    		'#saveCustomer': {
    			click: function(btn){//保存商城客户编号
    				var code = Ext.getCmp('b2ccusomter').getValue();
    				if(code!= null && code !=''){//保存
    					me.saveCustomer(code,btn);
    				}else{
    					showError("请选择客户编号");
    					Ext.getCmp('b2ccusomter').focus();
    				}
    			}
    		},
    		'#saveVendor': {
    			click: function(btn){//保存商城供应商编号
    				var code = Ext.getCmp('b2cvendor').getValue();
    				if(code!= null && code !=''){//保存
    					me.saveVendor(code,btn);
    				}else{
    					showError("请选择供应商编号");
    					Ext.getCmp('b2cvendor').focus();
    				}
    			}
    		},
    		'#saveSaleKind': {
    			click: function(btn){//保存商城客户编号
    				var code = Ext.getCmp('b2csalekind').getValue();
    				if(code!= null && code !=''){//保存
    					me.saveSaleKind(code,btn);
    				}else{
    					showError("请选择销售类型");
    					Ext.getCmp('b2csalekind').focus();
    				}
    			}
    		},
    		'#newCustomer': {//新增客户编号
    			click: function(){
    				me.FormUtil.onAdd('add_' + caller, '新增客户资料', 'jsps/scm/sale/customerBase.jsp?whoami=Customer!Base');
    			}
    		},
    		'#newVendor': {//新增供应商
    			click: function(){
    				me.FormUtil.onAdd('add_' + caller, '新增供应商资料', 'jsps/scm/purchase/vendor.jsp?whoami=Vendor');
    			}
    		},
    		'#tips':{
    			afterrender:function(e){
    				
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getB2CSetting: function(){
		//var main = parent.Ext.getCmp("content-panel"), me = this;
		//	main.getActiveTab().setLoading(true);//loading...
		/*var me = this;
		me.setLoading(true);*/
		var form = Ext.getCmp("form");
		Ext.Ajax.request({
				url : basePath +'b2c/getB2CSetting.action',
				params : {
					_noc: 1,
					caller:caller
					//data: unescape(Ext.JSON.encode(dd))
				},
				method : 'post',
				callback : function(opt, s, res){
					//me.setLoading(false);
					//main.getActiveTab().setLoading(false);
					var r = new Ext.decode(res.responseText);
					if(r.success){
	    				form.getForm().setValues(r.data);
		   			} else if(r.exceptionInfo){
		   				showError(r.exceptionInfo);
		   			}
				}
			});
	},
	startB2C : function(){
		var main = parent.Ext.getCmp("content-panel"), me = this;
			main.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath +'b2c/startB2C.action',
			params : {
				_noc: 1,
				caller: caller
			},
			method : 'post',
			callback : function(opt, s, res){
				main.getActiveTab().setLoading(false);//loading...
				var r = new Ext.decode(res.responseText);
				if(r.success){
    				 //重新加载页面window.location.href = basePath + "jsps/pm/bom/BOM.jsp";
					//me.getB2CSetting();
	   			} else if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			}
			}
		});
	},
	save:function(){
		var main = parent.Ext.getCmp("content-panel"), me = this;
			main.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath +'b2c/saveB2CSetting.action',
			params : {
				_noc: 1,
				caller: caller,
				param: code
			},
			method : 'post',
			callback : function(opt, s, res){
				main.getActiveTab().setLoading(false);//loading...
				var r = new Ext.decode(res.responseText);
				if(r.success){
    				 // window.location.href = basePath + "jsps/pm/mps/B2CSetting.jsp?whoami=B2CSetting";
    				  showMessage('提示', '保存成功!', 1000);
					  me.getB2CSetting();
	   			} else if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			}
			}
		});
	},
	saveCustomer : function(code,btn){
		var main = parent.Ext.getCmp("content-panel"), me = this;
			main.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath +'b2c/saveB2CCustomer.action',
			params : {
				_noc: 1,
				caller: caller,
				param: code
			},
			method : 'post',
			callback : function(opt, s, res){
				main.getActiveTab().setLoading(false);//loading...
				var r = new Ext.decode(res.responseText);
				if(r.success){
    				 // window.location.href = basePath + "jsps/pm/mps/B2CSetting.jsp?whoami=B2CSetting";
    				  showMessage('提示', '保存成功!', 1000);
					  me.getB2CSetting();
	   			} else if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			}
			}
		});
	},
	saveVendor : function(code,btn){
		var main = parent.Ext.getCmp("content-panel"), me = this;
			main.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath +'b2c/saveB2CVendor.action',
			params : {
				_noc: 1,
				caller: caller,
				param: code
			},
			method : 'post',
			callback : function(opt, s, res){
				main.getActiveTab().setLoading(false);//loading...
				var r = new Ext.decode(res.responseText);
				if(r.success){
					 showMessage('提示', '保存成功!', 1000);
					 me.getB2CSetting();
					//window.location.href = basePath + "jsps/pm/mps/B2CSetting.jsp?whoami=B2CSetting";
	   			} else if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			}
			}
		});
	},
	saveSaleKind : function(code,btn){
		var main = parent.Ext.getCmp("content-panel"), me = this;
			main.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({
			url : basePath +'b2c/saveB2CSaleKind.action',
			params : {
				_noc: 1,
				caller: caller,
				param: code
			},
			method : 'post',
			callback : function(opt, s, res){
				main.getActiveTab().setLoading(false);//loading...
				var r = new Ext.decode(res.responseText);
				if(r.success){
					 showMessage('提示', '保存成功!', 1000);
					 me.getB2CSetting();
    				 //window.location.href = basePath + "jsps/pm/mps/B2CSetting.jsp?whoami=B2CSetting";
	   			} else if(r.exceptionInfo){
	   				showError(r.exceptionInfo);
	   			}
			}
		});
	}
});