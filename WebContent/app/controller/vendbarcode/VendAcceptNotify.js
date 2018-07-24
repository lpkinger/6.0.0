Ext.QuickTips.init();
Ext.define('erp.controller.vendbarcode.VendAcceptNotify', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
	'core.form.Panel','vendbarcode.vendAcceptNotify.Form','vendbarcode.vendAcceptNotify.GridPanel','core.grid.Panel2','core.toolbar.Toolbar','core.trigger.MultiDbfindTrigger','core.trigger.HrOrgTreeDbfindTrigger',
	'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
	'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit',
	'core.button.Banned','core.button.ResBanned','core.form.MultiField','core.button.Confirm','core.button.Sync',
	'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn','core.trigger.AddDbfindTrigger',
	'core.form.FileField','core.button.Barcode','core.button.ConfirmDelivery','core.button.CancelDelivery'
      	],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.GridUtil = Ext.create('erp.util.GridUtil');
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({
    		'vendAcceptNotifyForm': {
    		   /* afterload: function(form) {
			    var items = form.items.items;
				Ext.each(items, function(item) {
					item.setReadOnly(true);
				});
			   }*/
		    },
    		'erpvendAcceptNotifyGrid': { 
    			itemclick: this.onGridItemClick,   			
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('AN_ID').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
			'erpSubmitButton' : {
				click : function(btn) {
					this.FormUtil.onSubmit(Ext.getCmp('AN_ID').value);
				}
			},
			'erpResSubmitButton' : {
				click : function(btn) {
					var status = Ext.getCmp('AN_STATUSCODE');
					this.FormUtil.onResSubmit(Ext.getCmp('AN_ID').value);
				}
			},
			'erpConfirmDeliveryButton' : {
                click: function(btn) {
                	me.FormUtil.setLoading(true);
                    Ext.Ajax.request({
                        url: basePath + 'vendbarcode/acceptNotify/confirmDelivery.action',
                        params: {
                            caller: caller,
                            id: Ext.getCmp('AN_ID').value
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                        	me.FormUtil.setLoading(false);
                            var res = new Ext.decode(response.responseText);
                            if (res.exceptionInfo) {
                                showError(res.exceptionInfo);
                                return;
                            } else {
                                if (res.success){
                            	showMessage("提示","确认送货成功！",1000);
                                window.location.reload();
                                }
                            }
                        }
                    });
                }
            
			},
			'erpCancelDeliveryButton' : {
                click: function(btn) {
                	me.FormUtil.setLoading(true);
                    Ext.Ajax.request({
                        url: basePath + '/vendbarcode/acceptNotify/cancelDelivery.action',
                        params: {
                            caller: caller,
                            id: Ext.getCmp('AN_ID').value
                        },
                        method: 'post',
                        callback: function(options, success, response) {
                        	me.FormUtil.setLoading(false);
                            var res = new Ext.decode(response.responseText);
                            if (res.exceptionInfo) {
                                showError(res.exceptionInfo);
                                return;
                            } else {
                                if (res.success){
                            	showMessage("提示","取消送货成功！",1000);
                                window.location.reload();
                                }
                            }
                        }
                    });
                }
            
			},
			'erpBarcodeButton':{
                click: function(btn) {
                	var me= this;
                	var barcodeinqty = false;
                	var grid = Ext.getCmp("gridAccept");
                    var id = Ext.getCmp("AN_ID").value;
                    var inoutNo=Ext.getCmp("AN_CODE").value;
                    var status = Ext.getCmp("AN_STATUSCODE").value;
                    var formCondition1 = "an_idIS" + id +"and an_codeIS '"+inoutNo+"'";
                    var gridCondition1 = "ban_anidIS" + id +" order by ban_id asc";
                    var linkCaller = 'Vendor!Baracceptnotify';
                        var result = 0;
                        Ext.Ajax.request({
                            url : basePath + 'common/getFieldData.action',
                            async: false,
                            params: {
                                caller: 'baracceptnotify',
                                field: 'count(ban_id)',
                                condition: 'ban_anid=' + id
                            },
                            method : 'post',
                            callback : function(opt, s, res){
                                var r = new Ext.decode(res.responseText);
                                if(r.exceptionInfo){
                                    showError(r.exceptionInfo);return;
                                } else if(r.success){
                                    result = r.data;
                                }
                            }
                        });
                        if(result > 0 ){  //存在已经生成的条码明细或者出库
                        	me.FormUtil.onAdd('addBarcode'+id, '条形码维护('+inoutNo+')', 'jsps/vendbarcode/setBarcode.jsp?_noc=1&whoami=' + linkCaller +'&key='+id+'&inoutno='+inoutNo+'&status='+status+'&formCondition=' + formCondition1 + '&gridCondition=' + gridCondition1);
                        }else{
                        	  formCondition1 = "and_anidIS" + id +" and an_codeIS'"+inoutNo+"'";
                              gridCondition1 = "and_anidIS" + id+" and and_inqty-nvl(and_barqty,0)>0 order by and_detno asc";
                        	  var win = new Ext.window.Window({
                                  id: 'win',
                                  height: '80%',
                                  width: '90%',
                                  maximizable: true,
                                  title:'<span><font color=blue>条码维护[送货通知单:'+inoutNo+']</font></span>',
                                  buttonAlign: 'center',
                                  layout: 'anchor',
                                  closeAction:'hide',
                                  items: [{
                                      tag: 'iframe',
                                      frame: true,
                                      anchor: '100% 100%',
                                      layout: 'fit',
                                      html: '<iframe id="iframe_' + linkCaller + '" src="' + basePath + 'jsps/vendbarcode/saveBarcode.jsp?_noc=1&whoami=' + linkCaller +'&key='+id+'&inoutno='+inoutNo+ '&status='+status+'&formCondition=' + formCondition1 + '&gridCondition=' + gridCondition1 + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
                                  }]
                              });
                              win.show();
                        }
                   }
			}
    	});
    }, 
    onGridItemClick: function(selModel, record){// grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});