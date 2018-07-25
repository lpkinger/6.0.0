Ext.QuickTips.init();
Ext.define('erp.controller.sys.init.SysDataCheck', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.BaseUtil'],
    views: ['sys.init.SysDataCheck'],
    init:function(){
    	var me = this;
    	this.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'button[id=organazition]':{
    			click:function(btn){
    				var cardpanel=Ext.getCmp('cardpanel');
    				cardpanel.getLayout().setActiveItem(0);
    				btn.addCls('btn-cardchange');
    				btn.setDisabled(true);
    				var btnpro=Ext.getCmp('process');
    				var btnfin=Ext.getCmp('finance');
    				var btnview=Ext.getCmp('view');
    				var btnJOB=Ext.getCmp('JOB');
    				if(btnpro.disabled){
    					btnpro.setDisabled(false);
    					btnpro.removeCls('btn-cardchange');
    				}
    				if(btnfin.disabled){
    					btnfin.setDisabled(false);
    					btnfin.removeCls('btn-cardchange');
    				}
    				if (btnview.disabled) {
    					btnview.setDisabled(false);
    					btnview.removeCls('btn-cardchange');
    				}
    				if(btnJOB.disabled){
    					btnJOB.setDisabled(false);
    					btnJOB.removeCls('btn-cardchange');
    				}    				
    			}
    		},
    		'button[id=process]':{
    			click:function(btn){
    				var cardpanel=Ext.getCmp('cardpanel');
    				cardpanel.getLayout().setActiveItem(1);
    				btn.addCls('btn-cardchange');
    				btn.setDisabled(true);
    				var btnorg=Ext.getCmp('organazition');
    				var btnfin=Ext.getCmp('finance');
    				var btnview=Ext.getCmp('view');
    				var btnJOB=Ext.getCmp('JOB');
    				if(btnorg.disabled){
    					btnorg.setDisabled(false);
    					btnorg.removeCls('btn-cardchange');
    				}
    				if(btnfin.disabled){
    					btnfin.setDisabled(false);
    					btnfin.removeCls('btn-cardchange');
    				}
    				if (btnview.disabled) {
    					btnview.setDisabled(false);
    					btnview.removeCls('btn-cardchange');
    				}
    				if(btnJOB.disabled){
    					btnJOB.setDisabled(false);
    					btnJOB.removeCls('btn-cardchange');
    				}    				
    			}
    		},    		
    		'button[id=finance]':{
    			click:function(btn){
    				var cardpanel=Ext.getCmp('cardpanel');
    				cardpanel.getLayout().setActiveItem(2);
    				btn.addCls('btn-cardchange');
    				btn.setDisabled(true);
    				var btnpro=Ext.getCmp('process');
    				var btnorg=Ext.getCmp('organazition');
    				var btnview=Ext.getCmp('view');
    				var btnJOB=Ext.getCmp('JOB');
    				if(btnpro.disabled){
    					btnpro.setDisabled(false);
    					btnpro.removeCls('btn-cardchange');
    				}
    				if(btnorg.disabled){
    					btnorg.setDisabled(false);
    					btnorg.removeCls('btn-cardchange');
    				}
    				if (btnview.disabled) {
    					btnview.setDisabled(false);
    					btnview.removeCls('btn-cardchange');
    				}
    				if(btnJOB.disabled){
    					btnJOB.setDisabled(false);
    					btnJOB.removeCls('btn-cardchange');
    				}    				
    			}
    		},
    		'button[id=view]':{
    			click:function(btn){
    				var cardpanel=Ext.getCmp('cardpanel');
    				cardpanel.getLayout().setActiveItem(3);
    				btn.addCls('btn-cardchange');
    				btn.setDisabled(true);
    				var btnpro=Ext.getCmp('process');
    				var btnorg=Ext.getCmp('organazition');
    				var btnfin=Ext.getCmp('finance');
    				var btnJOB=Ext.getCmp('JOB');
    				if(btnpro.disabled){
    					btnpro.setDisabled(false);
    					btnpro.removeCls('btn-cardchange');
    				}
    				if(btnorg.disabled){
    					btnorg.setDisabled(false);
    					btnorg.removeCls('btn-cardchange');
    				}
    				if(btnfin.disabled){
    					btnfin.setDisabled(false);
    					btnfin.removeCls('btn-cardchange');
    				}    				
    				if(btnJOB.disabled){
    					btnJOB.setDisabled(false);
    					btnJOB.removeCls('btn-cardchange');
    				}    				
    			}
    		},
    		'button[id=JOB]':{
    			click:function(btn){
    				var cardpanel=Ext.getCmp('cardpanel');
    				cardpanel.getLayout().setActiveItem(4);
    				btn.addCls('btn-cardchange');
    				btn.setDisabled(true);
    				var btnpro=Ext.getCmp('process');
    				var btnorg=Ext.getCmp('organazition');
    				var btnfin=Ext.getCmp('finance');
    				var btnview=Ext.getCmp('view');
    				if(btnpro.disabled){
    					btnpro.setDisabled(false);
    					btnpro.removeCls('btn-cardchange');
    				}
    				if(btnorg.disabled){
    					btnorg.setDisabled(false);
    					btnorg.removeCls('btn-cardchange');
    				}
    				if(btnfin.disabled){
    					btnfin.setDisabled(false);
    					btnfin.removeCls('btn-cardchange');
    				}    				
    				if (btnview.disabled) {
    					btnview.setDisabled(false);
    					btnview.removeCls('btn-cardchange');
    				}    				
    			}
    		},    		
    		'panel[id=cardpanel]':{
    			afterrender:function(panel){
    				panel.getLayout().setActiveItem(0);
    				var btn=Ext.getCmp('organazition');
    				btn.addCls('btn-cardchange');
    				btn.setDisabled(true);
    			}
    		},
    		'button[id=check]': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt;
    				grid.store.each(function(r){
    					r.set('check', '');
    				});
    				btn.setDisabled(true);
    				me.check(grid, 0, btn);
    			}
    		},
    		'button[id=check1]': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt;
    				grid.store.each(function(r){
    					r.set('check', '');
    				});
    				btn.setDisabled(true);
    				me.check(grid, 0, btn);
    			}
    		},
    		'button[id=check2]': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt;
    				grid.store.each(function(r){
    					r.set('check', '');
    				});
    				btn.setDisabled(true);
    				me.check(grid, 0, btn);
    			}
    		},
    		'button[id=check3]': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt;
    				grid.store.each(function(r){
    					r.set('check', '');
    				});
    				btn.setDisabled(true);
    				me.check(grid, 0, btn);
    			}
    		},
    		'button[id=check4]': {
    			click: function(btn) {
    				var grid = btn.ownerCt.ownerCt;
    				grid.store.each(function(r){
    					r.set('check', '');
    				});
    				btn.setDisabled(true);
    				me.jobcheck(grid, 0, btn);
    			}
    		},    		
    		'button[id=close]': {
    			click: function() {
    				me.BaseUtil.getActiveTab().close();
    			}
    		},
    		'button[id=close1]': {
    			click: function() {
    				me.BaseUtil.getActiveTab().close();
    			}
    		},
    		'button[id=close2]': {
    			click: function() {
    				me.BaseUtil.getActiveTab().close();
    			}
    		},
    		'button[id=close3]': {
    			click: function() {
    				me.BaseUtil.getActiveTab().close();
    			}
    		},
    		'button[id=close4]': {
    			click: function() {
    				me.BaseUtil.getActiveTab().close();
    			}
    		},    		
    		'gridpanel[id=check-grid]': {
    			afterrender: function(grid) {
    				grid.check = function(idx) {
    					var record = grid.store.getAt(idx);
    					record.set('check', '');
    					var btn = Ext.getCmp('check');
    					btn.setDisabled(true);
    					me.check(grid, record, btn);
    				};
    			}
    		},
    		'gridpanel[id=check-grid1]': {
    			afterrender: function(grid) {
    				grid.check = function(idx) {
    					var record = grid.store.getAt(idx);
    					record.set('check', '');
    					var btn = Ext.getCmp('check');
    					btn.setDisabled(true);
    					me.check(grid, record, btn);
    				};
    			}
    		},
    		'gridpanel[id=check-grid2]': {
    			afterrender: function(grid) {
    				grid.check = function(idx) {
    					var record = grid.store.getAt(idx);
    					record.set('check', '');
    					var btn = Ext.getCmp('check');
    					btn.setDisabled(true);
    					me.check(grid, record, btn);
    				};
    			}
    		},
    		'gridpanel[id=check-grid3]': {
    			afterrender: function(grid) {
    				grid.check = function(idx) {
    					var record = grid.store.getAt(idx);
    					record.set('check', '');
    					var btn = Ext.getCmp('check');
    					btn.setDisabled(true);
    					me.check(grid, record, btn);
    				};
    			}
    		},
    		'gridpanel[id=check-grid4]': {
    			afterrender: function(grid) {
    				grid.jobcheck = function(idx) {
    					var record = grid.store.getAt(idx);
    					record.set('check', '');
    					var btn = Ext.getCmp('check');
    					btn.setDisabled(true);
    					me.jobcheck(grid, record, btn);
    				};
    			}
    		}    		
    	});
    },
	setLoading : function(b) {
		var mask = this.mask;
		if (!mask) {
			this.mask = mask = new Ext.LoadMask(Ext.getBody(), {
				msg : "系统正在为您保存,请稍后...",
				msgCls : 'z-index:10000;'
			});
		}
		if (b)
			mask.show();
		else
			mask.hide();
	},
    check: function(grid, idx, btn) {
    	var me = this, r;
    	if(Ext.isNumber(idx)) {
    		r = grid.store.getAt(idx);
    	} else {
    		r = idx;
    	}
    	if(!r) {
    		btn.setDisabled(false);
    		return;
    	}
    	if (r.index==3)me.setLoading(true);
    	r.set('check', 'loading');
    	
    	var actions=r.get('action').split('#');
    	
    	
    	Ext.Array.each(actions,function(action){
    		Ext.Ajax.request({
    			url: basePath + action,
    			method: 'GET',
    			timeout: 600000,
    			callback: function(opt, s, re) {  
    				r.set('check', 'checked');
    				r.set('detail', null);
    				if (r.index==3) me.setLoading(false);
    				grid.toggleRow(r);
    				var rs = Ext.decode(re.responseText); 
    				if(rs.error) {
    					r.set('check', 'error');
    				}   				
    				if(rs.result) {
    					r.set('detail', rs.result);
    				}
    				if(rs.exceptionInfo) {//批量保存流程，异常信息处理
    					r.set('check', 'error');
    					r.set('detail', rs.exceptionInfo);
    				}
    				if(Ext.isNumber(idx) && idx!=2 && idx!=3 && idx!=4) {
    					me.check(grid, ++idx, btn);
    				} else {
    					btn.setDisabled(false);
    				}
    			}
        	});
    	});
    },
    jobcheck: function(grid, idx, btn) {
    	
    	var me = this, r;
    	if(Ext.isNumber(idx)) {
    		r = grid.store.getAt(idx);
    	} else {
    		r = idx;
    	}
    	if(!r) {
    		btn.setDisabled(false);
    		return;
    	}
    	if (r.index==3)me.setLoading(true);
    	r.set('check', 'loading');
    	
    	var actions;
    		actions=r.get('ACTION').split('#');
    	
    	Ext.Array.each(actions,function(action){
    		Ext.Ajax.request({
    			url: basePath + action,
    			method: 'GET',
    			timeout: 600000,
    			callback: function(opt, s, re) {  
    				r.set('check', 'checked');
    				r.set('detail', null);
    				if (r.index==3) me.setLoading(false);
//    				grid.toggleRow(r);
    				var rs = Ext.decode(re.responseText); 
    				if(rs.error) {
    					r.set('check', 'error');
    				}   				
    				if(rs.result) {
    					r.set('detail', rs.result);
    				}
    				if(rs.exceptionInfo) {//批量保存流程，异常信息处理
    					r.set('check', 'error');
    					r.set('detail', rs.exceptionInfo);
    				}
    				if(Ext.isNumber(idx)) {
    					console.log(idx);
    					me.jobcheck(grid, ++idx, btn);
    				} else {
    					btn.setDisabled(false);
    				}
					grid.componentLayout.childrenChanged = true;
					grid.doComponentLayout();    				
    				
    			}
        	});
    	});
    }    
});