Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.CalCredit', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'fa.fp.CalCredit', 'core.form.Panel',
	          'core.grid.Panel2', 'core.toolbar.Toolbar', 'core.grid.YnColumn','core.button.Sync','core.button.Close', 'core.button.Update',
	          'core.trigger.DbfindTrigger','core.form.YnField', 'core.button.RefreshCredit', 'core.form.FileField','core.trigger.MultiDbfindTrigger' ],
	          init : function() {
	        	  var me = this;
	        	  this.control({
	        		  'erpGridPanel2' : {
	        			  itemclick : this.onGridItemClick
	        		  },
	        		  'field[name=cd_sellercode]' : {
	        			  aftertrigger : function(f) {
	        				  if (f.value != null && f.value != '') {
	        					  me.GridUtil.loadNewStore(Ext.getCmp('grid'), {
	        						  caller: caller,
	        						  condition: 'cd_sellercode=\'' + f.value + '\''
	        					  });
	        				  }
	        				  var c = Ext.getCmp('cuc_custcode');
	        				  if(c && !Ext.isEmpty(c.value)){
	        					  me.GridUtil.loadNewStore(Ext.getCmp('grid'), {
	        						  caller: caller,
	        						  condition: 'cd_sellercode=\'' + f.value + '\' and cuc_custcode=\'' + c.value + '\''
	        					  });
	        				  }
	        			  },
	        			  afterrender : function(f) {
	        				  if (f.value != null && f.value != '') {
	        					  me.GridUtil.loadNewStore(Ext.getCmp('grid'), {
	        						  caller: caller,
	        						  condition: 'cd_sellercode=\'' + f.value + '\''
	        					  });
	        				  }
	        				  var c = Ext.getCmp('cuc_custcode');
	        				  if(c && !Ext.isEmpty(c.value)){
	        					  me.GridUtil.loadNewStore(Ext.getCmp('grid'), {
	        						  caller: caller,
	        						  condition: 'cd_sellercode=\'' + f.value + '\' and cuc_custcode=\'' + c.value + '\''
	        					  });
	        				  }
	        			  }
	        		  },
	        		  'field[name=cuc_custcode]' : {
	        			  aftertrigger : function(f) {
	        				  if (f.value != null && f.value != '') {
	        					  me.GridUtil.loadNewStore(Ext.getCmp('grid'), {
	        						  caller: caller,
	        						  condition: 'cuc_custcode=\'' + f.value + '\''
	        					  });
	        				  }
	        				  var s = Ext.getCmp('cd_sellercode');
	        				  if(s && !Ext.isEmpty(s.value)){
	        					  me.GridUtil.loadNewStore(Ext.getCmp('grid'), {
	        						  caller: caller,
	        						  condition: 'cuc_custcode=\'' + f.value + '\' and cd_sellercode=\'' + s.value + '\''
	        					  });
	        				  }
	        			  }
	        		  },
	        		  'erpCloseButton' : {
	        			  click : function(btn) {
	        				  me.FormUtil.beforeClose(me);
	        			  }
	        		  },
	        		  'erpRefreshCreditButton':{
	      				click: function(btn){
	      					var grid = Ext.getCmp('grid');
	      					grid.setLoading(true);
	      					Ext.Ajax.request({
	      						url : basePath + "fa/fp/CalCreditRefreshCredit.action",
	      						params: {
	      							param: Ext.getCmp('cuc_custcode') ? Ext.getCmp('cuc_custcode').value : ""
	      	                    },
	      						method : 'post',
	      						timeout: 300000,
	          		        	callback : function(options,success,response){
	          		        		grid.setLoading(false);
	          		        		var res = Ext.decode(response.responseText);
	          		        		if(res.exceptionInfo){
	          		        			showError(res.exceptionInfo);
	          		        			return;
	          		        		}
	          		        		if(res.success){
	          		        			showMessage("提示","刷新额度成功！");
	          		        			var s = Ext.getCmp('cd_sellercode'), c = Ext.getCmp('cuc_custcode')
	          		        			    cond = '';
		      	        				if(s && !Ext.isEmpty(s.value)){
		      	        					cond = 'cd_sellercode=\'' + f.value + '\'';
		      	        					if(c && !Ext.isEmpty(c.value)){
			      	        					cond = 'cd_sellercode=\'' + f.value + '\' and cuc_custcode=\'' + c.value + '\'';
			      	        				}
		      	        				} else {
		      	        					if(c && !Ext.isEmpty(c.value)){
			      	        					cond = 'cuc_custcode=\'' + c.value + '\'';
			      	        				}
		      	        				}
			  	        				me.GridUtil.loadNewStore(Ext.getCmp('grid'), {
	      	        						caller: caller,
	      	        						condition: cond
	      	        					});
	          		        		}
	          		        	}
	      					});
	      				}
	      			}
	        	  });
	          },
	          onGridItemClick : function(selModel, record) {//grid行选择
	        	  this.GridUtil.onGridItemClick(selModel, record);
	          },
	          getForm : function(btn) {
	        	  return btn.ownerCt.ownerCt;
	          }
});