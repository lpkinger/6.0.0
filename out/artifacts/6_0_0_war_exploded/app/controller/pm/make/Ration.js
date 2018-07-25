Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.Ration', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','pm.make.Ration','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.ResAudit',
      		'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
      		'core.button.ResSubmit',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
					this.onGridItemClick(selModel, record);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
	    			warnMsg("确定要删除单据吗?", function(btn){
	    				if(btn == 'yes'){
	    					var bomid= Ext.getCmp('ra_topbomid').value;
	    					me.setLoading(true);
	    					Ext.Ajax.request({
	    				   		url : basePath + 'pm/make/deleteRation.action',
	    				   		params: {
	    				   			id: bomid
	    				   		},
	    				   		method : 'post',
	    				   		callback : function(options,success,response){
	    				   			me.setLoading(false);
	    				   			var localJson = new Ext.decode(response.responseText);
	    				   			if(localJson.exceptionInfo){
	    			        			showError(localJson.exceptionInfo);return;
	    			        		}
	    			    			if(localJson.success){
	    				   				delSuccess(function(){
	    				   					me.FormUtil.beforeClose(me);							
	    								});//@i18n/i18n.js
	    				   			} else {
	    				   				delFailure();
	    				   			}
	    				   		}
	    					});
	    				}
	    			});
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addRation', '新增标准工时', 'jsps/pm/make/Ration.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ra_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var me = this;
    				var bomid= Ext.getCmp('ra_topbomid').value;
    				me.setLoading(true);//loading...
    				Ext.Ajax.request({
    			   		url : basePath + 'pm/make/submitRation.action',
    			   		params: {
    			   			id: bomid
    			   		},
    			   		method : 'post',
    			   		callback : function(options,success,response){
    			   			var localJson = new Ext.decode(response.responseText); 
    			   			if(localJson.exceptionInfo){
    	    	   				var str = localJson.exceptionInfo;
    	    	   				showError(str);
    	    	   			}else {
    	    	   				if(localJson.MultiAssign){
    	    	   					me.showAssignWin(localJson.assigns,localJson.nodeId);
    	    	   				}else {
    	    	   					showMessage('提示', '提交成功!', 1000);
    	    						window.location.reload();
    	    	   				}
    	    	   			}
    			   		}
    				});
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ra_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var me = this;
    				var bomid= Ext.getCmp('ra_topbomid').value;
    				me.setLoading(true);//loading...
    				Ext.Ajax.request({
    			   		url : basePath + 'pm/make/resSubmitRation.action',
    			   		params: {
    			   			id: bomid
    			   		},
    			   		method : 'post',
    			   		callback : function(options,success,response){
    			   			me.setLoading(false);
    			   			var localJson = new Ext.decode(response.responseText);
    			   			if(localJson.exceptionInfo){
    			   				showError(localJson.exceptionInfo);
    			   			}
    		    			if(localJson.success){
    		    				showMessage('提示', '反提交成功!', 1000);
    		    				window.location.reload();
    		    			}
    			   		}
    				});
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ra_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var me = this;
    				var bomid= Ext.getCmp('ra_topbomid').value;
    				me.setLoading(true);//loading...
    				Ext.Ajax.request({
    			   		url : basePath + 'pm/make/auditRation.action',
    			   		params: {
    			   			id: bomid
    			   		},
    			   		method : 'post',
    			   		callback : function(options,success,response){
    			   			var localJson = new Ext.decode(response.responseText); 
    			   			if(localJson.exceptionInfo){
    	    	   				var str = localJson.exceptionInfo;
    	    	   				showError(str);
    	    	   			}else {
    	    	   				if(localJson.MultiAssign){
    	    	   					me.showAssignWin(localJson.assigns,localJson.nodeId);
    	    	   				}else {
    	    	   					showMessage('提示', '审核成功!', 1000);
    	    						window.location.reload();
    	    	   				}
    	    	   			}
    			   		}
    				});
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ra_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var me = this;
    				var bomid= Ext.getCmp('ra_topbomid').value;
    				me.setLoading(true);//loading...
    				Ext.Ajax.request({
    			   		url : basePath + 'pm/make/resAuditRation.action',
    			   		params: {
    			   			id: bomid
    			   		},
    			   		method : 'post',
    			   		callback : function(options,success,response){
    			   			me.setLoading(false);
    			   			var localJson = new Ext.decode(response.responseText);
    			   			if(localJson.exceptionInfo){
    			   				showError(localJson.exceptionInfo);
    			   			}
    		    			if(localJson.success){
    		    				showMessage('提示', '反审核成功!', 1000);
    		    				window.location.reload();
    		    			}
    			   		}
    				});
    			}
    		},
    		'field[name=ra_topbomid]':{
    			beforerender: function(f){
    				Ext.defer(function(){
    					if(f.value != null & f.value != ''){
    						f.setReadOnly(true);
        				}
					}, 200);
				},
    			afterrender: function(f){
    				if(f.value != null & f.value != ''){
    					this.getRationStore('ra_topbomid=' + f.value);
    				}
    			},
    			aftertrigger: function(f){
    				if(f.value != null & f.value != ''){
    					this.getRationStore('ra_topbomid=' + f.value);
    				}
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getRationStore: function(condition){
		var me = this;
		var grid = Ext.getCmp('grid');
		grid.store.removeAll(false);
		me.BaseUtil.getActiveTab().setLoading(true);//loading...
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "common/singleGridPanel.action",
        	params: {
        		caller: "Ration",
        		condition: condition
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		me.BaseUtil.getActiveTab().setLoading(false);
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = [];
        		if(!res.data || res.data.length == 2){
        			me.GridUtil.add10EmptyItems(grid);
        		} else {
        			data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
        			if(data.length > 0){
            			grid.store.loadData(data);
            		}
        		}
        	}
        });
	},
	insert: function(topbomid){
		Ext.Ajax.request({//拿到grid的columns
        	url : basePath + "pm/make/insertRation.action",
        	params: {
        		caller: 'BOM left join BOMSTRUCT on BO_MOTHERCODE=BS_SONCODE left join CRAFT on BO_CRAFTCODE=CR_CODE left join CRAFTDETAIL on CR_ID=CD_CRID left join WORKCENTER on BO_WCCODE=wc_code',
    			condition: "BS_TOPBOMID=" + topbomid + " and (bs_sonbomid>0 or bs_idcode=0) and nvl(BS_SUPPLYTYPE,' ')<>'VIRTUAL' order by cd_detno asc,BS_LEVEL desc"
        	},
        	method : 'post',
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exceptionInfo){
        			showError(res.exceptionInfo);return;
        		}
        		var data = res.data;
        		var fpd = [];
        		if(data != null && data.length > 0){
        			Ext.each(data, function(d, index){
        				var da = {
        						ra_mothercode : d.bs_soncode,
        						crd_step : d.cd_stepcode,
        						ra_stepname : d.cd_stepname,
        						ra_wccode : d.bo_wccode,
        						ra_wcname : d.wc_name
        				};
        				fpd[index] = da;
        			});
        			Ext.getCmp('grid').store.loadData(fpd);
        		}
        	}
		});
	},
	setLoading : function(b) {
		var mask = this.mask;
		if (!mask) {
			this.mask = mask = new Ext.LoadMask(Ext.getBody(), {
				msg : "处理中,请稍后...",
				msgCls : 'z-index:10000;'
			});
		}
		if (b)
			mask.show();
		else
			mask.hide();
	}
});