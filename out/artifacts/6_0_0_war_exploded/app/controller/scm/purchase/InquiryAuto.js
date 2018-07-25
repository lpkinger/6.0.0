Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.InquiryAuto', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.purchase.InquiryAuto','core.grid.Panel2','core.toolbar.Toolbar','core.form.FileField','core.button.DeleteAuto',
      		'core.button.Save','core.button.Update','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload',
  				'core.button.Audit','core.button.Close','core.button.Delete','core.button.DeleteDetail','core.button.ResSubmit','core.button.DeleteAutoDet',
  				'core.button.ResAudit','core.button.HistoryQuotation','core.button.HistoryInPrice','core.button.UpdateMaxlimitInfo','core.button.UpdatePurcVendor',
  				'core.button.UpdateInfo','core.button.Nullify','core.button.CopyAll','core.button.DownLoadFile','core.trigger.MultiDbfindTrigger',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.TurnPurcPrice','core.button.AgreeAutoPrice','core.grid.YnColumn','core.button.SubmitAudit','core.button.ResSubmitAudit'
      	],
    init:function(){
    	var me = this;
    	me.alloweditor = true;
    	me.allowAuditProcess=false;
    	this.control({ 
    		'field[name=in_purpose]':{
				beforerender: function(field){
					field.setReadOnly(false);
				}
			},
    		'field[name=in_remark]':{
				beforerender: function(field){
					field.setReadOnly(false);
				}
			},
			'erpUpdateInfoButton':{
				afterrender: function(btn){
    				var status = Ext.getCmp('in_statuscode');
    				if(status && status.value == 'NULLIFIED'){
    					btn.hide();
    				}
    			},
    			click:function(){
    				var purpose= Ext.getCmp('in_purpose'), remark= Ext.getCmp('in_remark');
    				if(purpose && remark){
    					me.updateInfo(purpose.value, remark.value, Ext.getCmp('in_id').value);
    				}
    			}
    		},
    		'erpNullifyButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('in_statuscode'), checkstatuscode = Ext.getCmp('in_checkstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    				if(checkstatuscode && checkstatuscode.value == 'APPROVED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onNullify(Ext.getCmp('in_id').value);
    			}
    		},
    		'erpFormPanel':{
    			afterrender:function(){
    				me.BaseUtil.getSetting('InquiryAuto', 'allowAuditProcess', function(bool) {
    					if(bool) me.allowAuditProcess=true;    					
    		        },false);
    			}
    		},
    		'erpGridPanel2': {
    			afterrender: function(grid){
    				var status = Ext.getCmp('in_statuscode');
    				if(status && status.value != 'ENTERING' && status.value != 'COMMITED'){
    					Ext.each(grid.columns, function(c){
    						c.setEditor(null);
    					});
    				}
    				Ext.defer(function(){
    					var f = Ext.getCmp('in_id');
    					if(f && f.value > 0)
    						me.getStepWise(f.value, function(data){
    							grid.store.each(function(d){
    								var dets = [], id = d.get('id_id');
    								if(id && id > 0) {
    									Ext.Array.each(data, function(t){
    										if(t.idd_idid == id)
    											dets.push(t);
    									});
    									d.set('dets', dets);
    								}
    							});
    						});
    				}, 50);    			
    			},
    			itemclick: this.onGridItemClick
    		},
    		'erpDownLoadFileButton':{
    			click:function(btn){
    				if (!Ext.fly('ext-attach-download')) {  
						var frm = document.createElement('form');  
						frm.id = 'ext-attach-download';  
						frm.className = 'x-hidden';
						document.body.appendChild(frm);
					}
					Ext.Ajax.request({
						url: basePath + btn.url,
						method: 'post',
						form: Ext.fly('ext-attach-download'),
						isUpload: true
					});
    			}
    		},
    		'gridcolumn[dataIndex=id_purcvendcode]':{
    		    beforerender:function(c){
    		          c.autoEdit=true;  
    		    }
    		},
    		'gridcolumn[dataIndex=id_purcvendname]':{
    		    beforerender:function(c){
    		          c.autoEdit=true;  
    		    }
    		},
    		'gridcolumn[dataIndex=id_purccurrency]':{
    		    beforerender:function(c){
    		          c.autoEdit=true;  
    		    }
    		},
    		'gridcolumn[dataIndex=id_purctaxrate]':{
    		    beforerender:function(c){
    		          c.autoEdit=true;  
    		    }
    		},
    		'gridcolumn[dataIndex=id_purcprice]':{
    		    beforerender:function(c){
    		          c.autoEdit=true;  
    		    }
    		},
    		'gridcolumn[dataIndex=id_remark]':{
    		    beforerender:function(c){
    		          c.autoEdit=true;  
    		    }
    		},
    		'field[name=in_currency]': {
    			beforetrigger: function(field) {
    				var t = field.up('form').down('field[name=in_date]'),
    					value = t.getValue();
    				if(value) {
    					field.findConfig = 'cm_yearmonth=' + Ext.Date.format(value, 'Ym');
    				}
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				var grid = Ext.getCmp('grid');
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				// maz 批量询价保存前先取主表供应商第一个赋值过去
    				var in_prodtype = Ext.getCmp('in_prodtype');
    				if(in_prodtype && in_prodtype.value == '批量询价'){
    					var vendor = Ext.getCmp('in_batchvendor').value.split('#')[0];
    					grid.getStore().each(function(item){
    						if(item.dirty){
    							item.set('id_vendcode', vendor);
    							item.set('id_vendname','')
    							item.set('id_currency', 'RMB');
    						}
    					})
    				}
    				var bool = true;
    				//供应商必填
    				var start = Ext.getCmp('in_recorddate').value,
    				end = Ext.getCmp('in_enddate').value;
    				if(!Ext.isEmpty(end)){
    					if(Ext.Date.format(end,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
        					bool=false;
        					showError('有效期小于当前日期，不能保存!');return;
        				}
    				}
    				grid.getStore().each(function(item){
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['id_vendcode'] == null || item.data['id_vendcode'] == ''){
    							bool = false;
    							showError('明细表第' + item.data['id_detno'] + '行的供应商为空');return;
    						}
    						if(item.data['id_myfromdate'] ==null){
    							item.set('id_myfromdate', start);
    						}
//    						if(!Ext.isEmpty(end)){
//	    						if(item.data['id_mytodate'] == null){
//	    							item.set('id_mytodate', end);
//	    						}
//    						}
    					}
    				});
    				if(bool){
    					this.beforeSave();
    				}
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var bool = true;
    				//供应商必填
    				var grid = Ext.getCmp('grid'),
    					fromDate = Ext.getCmp('in_recorddate').value,
    					end = Ext.getCmp('in_enddate').value;
    				if(!Ext.isEmpty(end)){
	    				if(Ext.Date.format(end,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
	    					bool=false;
	    					showError('有效期小于当前日期，不能更新!');return;
	    				}
    				}
    				grid.getStore().each(function(item){
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['id_vendcode'] == null || item.data['id_vendcode'] == ''){
    							bool = false;
    							showError('明细表第' + item.data['id_detno'] + '行的供应商为空');return;
    						}
//    						if(!Ext.isEmpty(end)){
//    							if(item.data['id_mytodate'] ==null &&item.data['id_myfromdate'] ==null ){
//    								item.set('id_mytodate',end);
//    							}
//    						}
    						if(item.data['id_myfromdate'] ==null ){
    							item.set('id_myfromdate',fromDate);
    						}
    					}
    				});
    				if(bool){
    					this.beforeUpdate();
    				}
    			}
    		},
    		'erpUpdatePurcVendorButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('in_statuscode');
    				var checkstatus = Ext.getCmp('in_checkstatus').value;
    				if((status && status.value != 'AUDITED')||(checkstatus&&checkstatus=="已批准")){
    					btn.hide();
    				} 				
    			},
    			click: function(btn){				
    					this.beforeUpdate();
    			}
    		},
    		'erpDeleteAutoButton' : {
    			afterrender:function(btn){
    				var status = Ext.getCmp('in_checkstatuscode');
    				if(status && status.value!='ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var id = Ext.getCmp('in_id').value;
    				var me = this;
    				warnMsg("删除后将返回到已报价询价,是否确定删除?", function(b){
    					if(b=='yes'){
    						Ext.Ajax.request({
    							url: basePath + 'scm/purchase/deleteAuto.action',
    							params: {
    								caller : caller,
    								id : id
    							},
    							callback: function(opt, s, r) {
    								var rs = Ext.decode(r.responseText);
    								if(rs.exceptionInfo) {
    									showError(rs.exceptionInfo);
    								} else{
    									delSuccess(function(){
    										me.FormUtil.onClose();							
    									});//@i18n/i18n.js
    								}
    							}
    						});
    					}
    				})
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addInquiryAuto', '新增询价单', 'jsps/scm/purchase/InquiryAuto.jsp');
    			}
    		},
    		'erpUpdateMaxlimitInfoButton':{
				afterrender:function(btn){
    				btn.setDisabled(true);
    			},
				click: function(btn){
    				var id=btn.ownerCt.ownerCt.ownerCt.items.items[1].selModel.selected.items[0].data["id_id"];
    				var idstatus=btn.ownerCt.ownerCt.ownerCt.items.items[1].selModel.selected.items[0].data["id_status"];
    				if(idstatus!='已报价'){
    					showError("只能针对已报价的询价做限购");
    				}else{
    					var formCondition="id_id IS"+id;
        				var linkCaller='InquiryAutoMaxlimit';    				
        				var win = new Ext.window.Window({  
    						id : 'win',
    						height : '90%',
    						width : '95%',
    						maximizable : true,
    						buttonAlign : 'center',
    						layout : 'anchor',
    						items : [ {
    							tag : 'iframe',
    							frame : true,
    							anchor : '100% 100%',
    							layout : 'fit',
    							 html : '<iframe id="iframe_'+linkCaller+'" src="'+basePath+'jsps/scm/purchase/InquiryAutoMaxlimit.jsp?_noc=1&whoami='+linkCaller+'&formCondition='+formCondition+'" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
    						} ],
                            listeners:{
                              'beforeclose':function(view ,opt){
                            	   //grid  刷新一次
                            	  var grid=Ext.getCmp('grid');
                            	  var gridParam = {caller: caller, condition: gridCondition};
                            	  grid.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', gridParam, "");
                            	  Ext.getCmp('updateMaxlimitInfo').setDisabled(true);
                              }	
                            }
    					});
     					win.show(); 
    				}    				
    			}
			},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpResSubmitAuditButton':{
				afterrender:function(btn){   				
				var status=Ext.getCmp("in_statuscode");
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
				},
				click:function(btn){   					
					this.ResSubmitAudit(Ext.getCmp(me.getForm(btn).keyField).value);   					 
				}   			
    		},
    		'erpSubmitAuditButton':{
    			afterrender: function(btn){
    				var status = Ext.getCmp('in_statuscode');
    				if(!(me.allowAuditProcess && status && status.value == 'ENTERING'))
    					btn.hide();
    			},
    			click: function(btn){
    				this.SubmitAudit(Ext.getCmp(me.getForm(btn).keyField).value);   
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('in_statuscode');
    				var checkstatus = Ext.getCmp('in_checkstatuscode').value;
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}else if(checkstatus&&checkstatus!=null&&checkstatus!='ENTERING'){
    					btn.hide();
    				}   				
    			},
    			click: function(btn){
    				var form = Ext.getCmp('form');
    				var id = Ext.getCmp('in_id').value;
    				var url = 'scm/purchase/submitInquiryAuto.action';
				    me.FormUtil.getActiveTab().setLoading(true);
    				Ext.Ajax.request({
    			   		url : basePath + url,
    			   		params: {
    			   			id: id,
    			   			caller:caller
    			   		},
    			   		method : 'post',
    			   		callback : function(options,success,response){
    			   			me.FormUtil.getActiveTab().setLoading(false);
    			   			var localJson = new Ext.decode(response.responseText);
    			   			if(localJson.exceptionInfo){
    			   				showError(localJson.exceptionInfo);
    			   			}
    		    			if(localJson.success){
		    					me.FormUtil.getMultiAssigns(id,caller,form);
    		    				window.location.reload();
    		    			}
    			   		 }
    				  });
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('in_checkstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('in_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('in_statuscode');
    				if(!((me.allowAuditProcess && status && status.value == 'COMMITED')||(!me.allowAuditProcess && status && status.value == 'ENTERING')))
    					btn.hide();
    			},
    			click: function(btn){
    				var end = Ext.getCmp('in_enddate').value;
    				if(!Ext.isEmpty(end)){
	    				if(Ext.Date.format(end,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
	    					bool=false;
	    					showError('有效期小于当前日期，不能审核!');return;
	    				}
    				}
    				//2017060197   maz  询价单修改FORM后检查是否修改FORM，否则不允许审核
    				var me = this;
    				var form = Ext.getCmp('form');
    				if(form && form.getForm().isValid()){
	    				var s = me.checkFormDirty(form);
	    				if(s!=''&&s!='<br/>'){
	    					showError('您已修改过该单据，请先更新');
	    					return;
	    				}
    				}
    				me.FormUtil.onAudit(Ext.getCmp('in_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('in_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('in_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('in_id').value);
    			}
    		},
    		'erpAgreeAutoPriceButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('in_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var param = new Array();
    				var me = this ;
    				var grid = Ext.getCmp('grid');
					param = me.getGridStore(grid);
					param = param == null ? [] : "[" + param.toString().replace(/\\/g,"%") + "]";
    				Ext.Ajax.request({
    			   		url : basePath + 'scm/purchase/agreeAutoPrice.action',
    			   		params: {
    			   			id: Ext.getCmp('in_id').value,
    			   			param: param
    			   		},
    			   		method : 'post',
    			   		callback : function(options,success,response){
    			   			me.FormUtil.getActiveTab().setLoading(false);
    			   			var localJson = new Ext.decode(response.responseText);
    			   			if(localJson.exceptionInfo){
    			   				showError(localJson.exceptionInfo);
    			   			}
    		    			if(localJson.success){
    		    				Ext.Msg.alert("提示","最终判定成功!");
    			   			}
    			   		}
    				});
    			}
    		},
    		'erpTurnPurcPriceButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('in_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
       				warnMsg("确定要转入物料核价单吗?", function(btn){
    					if(btn == 'yes'){
    						me.FormUtil.getActiveTab().setLoading(true);//loading...
    	    				Ext.Ajax.request({
    	    			   		url : basePath + 'scm/purchase/turnAutoPurcPrice.action',
    	    			   		params: {
    	    			   			id: Ext.getCmp('in_id').value
    	    			   		},
    	    			   		method : 'post',
    	    			   		callback : function(options,success,response){
    	    			   			me.FormUtil.getActiveTab().setLoading(false);
    	    			   			var localJson = new Ext.decode(response.responseText);
    	    			   			if(localJson.exceptionInfo){
    	    			   				showError(localJson.exceptionInfo);
    	    			   			}
    	    		    			if(localJson.success){
    	    		    				turnSuccess(function(){
    	    		    					var id = localJson.id;
    	    		    					var url = "jsps/scm/purchase/purchasePrice.jsp?formCondition=pp_id=" + id + 
    	    		    						"&gridCondition=ppd_ppid=" + id;
    	    		    					me.FormUtil.onAdd('PurchasePrice' + id, '物料核价单' + id, url);
    	    		    				});
    	    			   			}
    	    			   		}
    	    				});
    					}
    				});
    			}
    		},
    		/**
    		 * 查看历史入库价
    		 */
    		'button[id=historyin]': {
    			click: function(btn){
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				if(record){
    					var prod = record.data['id_prodcode'];
    					btn.getWin(prod);
    					/*var prod = record.data['id_prodcode'];
    					var win = Ext.getCmp('in-win');
    					if(win == null){
    						win = Ext.create('Ext.window.Window', {
        						id: 'in-win',
        						width: '80%',
        						height: '100%',
        						maximizable : true,
        						layout: 'anchor',
        						closeAction: 'hide',
        						items: [Ext.create('erp.view.core.grid.Panel2', {
        							id: 'inhistory',
        							anchor: '100% 100%',
        							caller: 'ProdInOut!In!History',
        							condition: "pd_prodcode='" + prod + "' order by pd_prodmadedate desc",
        							bbar: null
        						})],
        						setMyTitle: function(code){//@param code 料号
        							this.setTitle('编号:<font color=blue>' + code + '</font>&nbsp;的历史入库价&nbsp;&nbsp;' + 
                							'<input type="button" value="上一条" onClick="javascript:Ext.getCmp(\'in-win\').prev();" style="cursor: pointer;color:gray;font-size:13px;"/>' + 
                							'<input type="button" value="下一条" onClick="javascript:Ext.getCmp(\'in-win\').next();" style="cursor: pointer;color:gray;font-size:13px;"/>');
        						},
        						reload: function(code){//@param code 料号
        							var g = this.down('grid[id=inhistory]');
    								g.GridUtil.loadNewStore(g, {
    									caller: g.caller,
    									condition: "pd_prodcode='" + code + "' order by pd_prodmadedate desc"
    								});
    								this.setMyTitle(code);
        						},
        						prev: function(){//查看上一条
        							var item = Ext.getCmp('grid').prev();
        							if(item != null){
        								this.reload(item.data['id_prodcode']);
        							}
        						},
        						next: function(){//查看下一条
        							var item = Ext.getCmp('grid').next();
        							if(item != null){
        								this.reload(item.data['id_prodcode']);
        							}
        						}
        					});
    						win.setMyTitle(prod);
        					win.show();
    					} else {
        					win.reload(prod);
        					win.show();
        				}*/
    				} else {
    					alert("请先选择明细!");
    				}
    			}
    		},
    		/**
    		 * 查看历史报价
    		 */
    		'button[id=historyquo]': {
    			afterrender : function(btn){
    				btn.show();
    			},
    			click: function(){
    				var record = Ext.getCmp('grid').selModel.lastSelected;
    				if(record){
    					var prod = record.data['id_prodcode'];
    					var win = Ext.getCmp('history-win');
    					if(win == null){
    						win = Ext.create('Ext.window.Window', {
        						id: 'history-win',
        						width: '80%',
        						height: '100%',
        						maximizable : true,
        						layout: 'anchor',
        						closeAction: 'destory',
        						setMyTitle: function(code){//@param code 料号
        							this.setTitle('编号:<font color=blue>' + code + '</font>&nbsp;的报价历史&nbsp;&nbsp;' + 
                							'<input type="button" value="上一条" onClick="javascript:Ext.getCmp(\'history-win\').prev();" style="cursor: pointer;color:gray;font-size:13px;"/>' + 
                							'<input type="button" value="下一条" onClick="javascript:Ext.getCmp(\'history-win\').next();" style="cursor: pointer;color:gray;font-size:13px;"/>');
        						},
        						reload: function(code){//@param code 料号
        							var g = this.down('grid[id=history]');
    								g.GridUtil.loadNewStore(g, {
    									caller: g.caller,
    									condition: "id_prodcode='" + code + "' and in_status='已审核' and in_checkstatus='已批准' "
    								});
    								g = this.down('grid[id=invid]');
    								g.GridUtil.loadNewStore(g, {
    									caller: g.caller,
    									condition: "ppd_prodcode='" + code + "' AND ppd_statuscode='VALID'"
    								});
    								this.setMyTitle(code);
        						},
        						prev: function(){//查看上一条
        							var item = Ext.getCmp('grid').prev();
        							if(item != null){
        								this.reload(item.data['id_prodcode']);
        							}
        						},
        						next: function(){//查看下一条
        							var item = Ext.getCmp('grid').next();
        							if(item != null){
        								this.reload(item.data['id_prodcode']);
        							}
        						}
        					});
    						win.setMyTitle(prod);
        					win.show();
        					win.add(Ext.create('erp.view.core.grid.Panel2', {
    							id: 'history',
    							anchor: '100% 60%',
    							caller: 'InquiryAuto!History',
    							condition: "id_prodcode='" + prod + "' and in_status='已审核' and in_checkstatus='已批准' ",
    							bbar: null,
    							listeners: {
    								reconfigure: function(){
    		        					win.add(Ext.create('erp.view.core.grid.Panel2', {
	    	    							id: 'invid',
	    	    							title: '现有效价格',
	    	    							anchor: '100% 40%',
	    	    							caller: 'PurchasePrice!Invid',
	    	    							condition: "ppd_prodcode='" + prod + "' AND ppd_statuscode='VALID'",
	    	    							bbar: null
	    	    						}));
    								}
    							}
    						}));
    					} else {
        					win.reload(prod);
        					win.show();
        				}
    				} else {
    					alert("请先选择明细!");
    				}
    			}
    		},
    		/**
    		 * 分段询价按钮
    		 */
    		'#stepWiseInquiryAuto' : {
    			afterrender: function(b) {
    				Ext.defer(function(){
    					var f = Ext.getCmp('in_statuscode');
    					if(f && f.value != 'ENTERING')
    						b.hide();
    				}, 100);
    			},
    			click: function(b) {
    				var record = b.ownerCt.ownerCt.selModel.lastSelected;
    				if(record)
    					me.onStepWiseClick(record);
    			}
    		},
    		'erpCopyButton': {
                click: function(btn) {
                    me.copy();
                }
            }
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    	var grid=selModel.ownerCt;
    	var show = false;
    	Ext.Array.each(grid.necessaryFields, function(field) {
    		var fieldValue=record.data[field];
    		if(fieldValue==undefined||fieldValue==""||fieldValue==null){
    			show = true;
    			return; 
    		}
        });
        var attach = record.data['id_attach'];
        if(attach){
        	btn = Ext.getCmp('downloadfile');
        	btn && btn.setDisabled(false);
        	btn.url = 'common/downloadbyId.action?id='+attach.substring(0,attach.indexOf(';'));
        }else{
        	btn = Ext.getCmp('downloadfile');
        	btn && btn.setDisabled(true);
        }
    	if(show){
    		var btn = Ext.getCmp('updateMaxlimitInfo');
        	btn && btn.setDisabled(true);
        	btn = Ext.getCmp('stepWiseInquiryAuto');
        	btn && btn.setDisabled(true);
        	btn = Ext.getCmp('historyquo');
        	btn && btn.setDisabled(true);
        	btn = Ext.getCmp('historyin');
        	btn && btn.setDisabled(true);
    	} else {
    		var btn = Ext.getCmp('updateMaxlimitInfo');
        	btn && btn.setDisabled(false);
        	btn = Ext.getCmp('stepWiseInquiryAuto');
        	btn && btn.setDisabled(false);
        	btn = Ext.getCmp('historyquo');
        	btn && btn.setDisabled(false);
        	btn = Ext.getCmp('historyin');
        	btn && btn.setDisabled(false);
		}    	
    	var btn = Ext.getCmp('deleteAutoDet');
    	btn && btn.setDisabled(false);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	/**
	 * 分段询价
	 */
	onStepWiseClick: function(record) {
		var fields = this.getStepFields(record.get('dets')), me = this;
		Ext.create('Ext.window.Window', {
			autoShow: true,
			title: '分段询价',
			width: 300,
			height: 400,
			layout: 'anchor',
			items: [{
				anchor: '100% 100%',
				xtype: 'form',
				items: fields,
				bodyStyle: 'background: #f1f2f5;',
				defaults: {
					margin: '5'
				}
			}],
			buttonAlign: 'center',
			buttons: [{
				text: '确定',
				handler: function(b) {
					me.onStepConfirm(record, b.ownerCt.ownerCt.down('form'), function(){
						b.ownerCt.ownerCt.close();
					});
				}
			}, {
				text: '取消',
				handler: function(b) {
					b.ownerCt.ownerCt.close();
				}
			}]
		});
	},
	getStepFields: function(dets) {
		if(!dets || dets.length == 0)
			dets = [{idd_lapqty: 0},{},{},{},{}];
		var fields = [], me = this;
		Ext.Array.each(dets, function(d){
			fields.push({
				xtype: 'numberfield',
				fieldLabel: '数量 ≥ ',
				labelWidth: 60,
				hideTrigger: true,
				name: 'idd_lapqty',
				value: d.idd_lapqty,
				dataId: d.idd_id,
				editable: (d.idd_lapqty == null || d.idd_lapqty > 0),
				flex: 1
			});
		});
		fields.push({
			xtype: 'button',
			iconCls: 'x-button-icon-add',
			handler: function(b) {
				me.onStepAdd(b.ownerCt);
			}
		});
		return fields;
	},
	onStepAdd: function(form) {
		var fields = form.items.items;
		if(fields.length > 11){
			showError('最多支持10个分段！');
		} else {
			form.insert(fields.length - 1, {
				xtype: 'numberfield',
				fieldLabel: '数量 ≥ ',
				labelWidth: 60,
				hideTrigger: true,
				name: 'idd_lapqty',
				flex: 1
			});
		}
	},
	onStepConfirm: function(record, form, callback) {
		var dets = [], steps = [], err = [];
		form.getForm().getFields().each(function(field){
			if('idd_lapqty' == field.name && field.value != null) {
				dets.push({idd_lapqty: field.value, idd_id: field.dataId});
				if(steps.indexOf(field.value) == -1)
					steps.push(field.value);
				else
					err.push('数量：' + field.value);
			}
		});
		if(err.length > 0) {
			showError('分段数量填写重复！<br>' + err.join('<br>'));
			return;
		}
		Ext.Array.sort(dets, function(a, b){
			return a.idd_lapqty > b.idd_lapqty;
		});
		record.set('dets', dets);
		record.dirty = true;
		record.modified = record.modified || {};
		record.modified['id_lapqty'] = true;
		callback.call(null);
	},
	beforeSave: function(){
		var me = this;
		var form = Ext.getCmp('form'), id = Ext.getCmp(form.keyField).value;
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.isEmpty(id) || id == 0 || id == '0'){
			me.FormUtil.getSeqId(form);
		}
		var detail = Ext.getCmp('grid');
		var dets = [];
		detail.store.each(function(record, i){
			if(!me.GridUtil.isBlank(detail, record.data)) {
				if(record.get('id_id') == null || record.get('id_id') == 0){
					record.set('id_id', -1 * i);
				}
				var s = record.get('dets') || [];
				Ext.Array.each(s, function(t, i){
					t.idd_id = t.idd_id || 0;
					t.idd_idid = String(record.get('id_id'));
					dets.push(t);
				});
			}
		});
		me.FormUtil.beforeSave(me, Ext.encode(dets));
	},
	getGridStore: function(grid){
		if(grid == null){
			grid = Ext.getCmp('grid');
		}
		var me = this,
			jsonGridData = new Array();
		var form = Ext.getCmp('form');
		if(grid!=null){
			var s = grid.getStore().data.items;//获取store里面的数据
			for(var i=0;i<s.length;i++){//将grid里面各行的数据获取并拼成jsonGridData
				var data = s[i].data;
				dd = new Object();
				if(s[i].dirty){
					Ext.each(grid.columns, function(c){
						if((c.logic != 'ignore') && c.dataIndex){//只需显示，无需后台操作的字段，自动略去
							if(c.xtype == 'datecolumn'){
								c.format = c.format || 'Y-m-d';
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], c.format);
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), c.format);//如果用户没输入日期，或输入有误，就给个默认日期，
									}else  dd[c.dataIndex]=null;
								}
							} else if(c.xtype == 'datetimecolumn'){
								if(Ext.isDate(data[c.dataIndex])){
									dd[c.dataIndex] = Ext.Date.format(data[c.dataIndex], 'Y-m-d H:i:s');//在这里把GMT日期转化成Y-m-d H:i:s格式日期
								} else {
									if(c.editor&&c.logic!='unauto'){
										dd[c.dataIndex] = Ext.Date.format(new Date(), 'Y-m-d H:i:s');//默认日期，
									}
								}
							} else if(c.xtype == 'numbercolumn'){//赋个默认值0吧，不然不好保存
								if(data[c.dataIndex] == null || data[c.dataIndex] == '' || String(data[c.dataIndex]) == 'NaN'){
									dd[c.dataIndex] = '0';//也可以从data里面去掉这些字段
								} else {
									dd[c.dataIndex] = "" + s[i].data[c.dataIndex];
								}
							} else {
								dd[c.dataIndex] = s[i].data[c.dataIndex];
							}
							if (c.defaultValue && (dd[c.dataIndex] == null || dd[c.dataIndex] == '0')) {
								dd[c.dataIndex] = c.defaultValue;
							}
						}
					});
					jsonGridData.push(Ext.JSON.encode(dd));
				}
			}
		}
		return jsonGridData;
	},
	beforeUpdate: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}		
		var detail = Ext.getCmp('grid');
		var dets = [];
		detail.store.each(function(record, i){
			if(!me.GridUtil.isBlank(detail, record.data)) {
				if(record.get('id_id') == null || record.get('id_id') == 0){
					record.set('id_id', -1 * i);
				}
				var s = record.get('dets') || [];
				Ext.Array.each(s, function(t, i){
					t.idd_id = t.idd_id || 0;
					t.idd_idid = String(record.get('id_id'));
					dets.push(t);
				});
			}
		});
		me.FormUtil.onUpdate(me, false, null, Ext.encode(dets));
	},
	getStepWise: function(in_id, callback) {
		Ext.Ajax.request({
			url: basePath + 'scm/purchase/InquiryAuto/det.action',
			params: {
				in_id: in_id
			},
			callback: function(opt, s, r) {
				if(s) {
					var rs = Ext.decode(r.responseText);
					callback.call(null, rs);
				}
			}
		});
	},
	SubmitAudit:function(id){
		var me = this;
		var form = Ext.getCmp('form');	
		if(form && form.getForm().isValid()){
			var s = me.FormUtil.checkFormDirty(form);
			if(s!=''&&s!='<br/>'){
				showError('您已修改过该单据，请先更新');
				return;
			}
			Ext.Ajax.request({
		   		url : basePath + form.submitAuditUrl,
		   		params: {
		   			id: id
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
					var localJson = new Ext.decode(response.responseText);
					if(localJson.success){
						 me.FormUtil.getMultiAssigns(id, caller+'!Audit',form);
					} else {
						if(localJson.exceptionInfo){
							var str = localJson.exceptionInfo;
							if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
								str = str.replace('AFTERSUCCESS', '');
								 me.FormUtil.getMultiAssigns(id, caller+'!Audit', form,me.FormUtil.showAssignWin);
							} 
							showMessage("提示", str);
						}
					}
				}
			});
		}else{ 
			me.FormUtil.checkForm(); 
		}
	},
	checkFormDirty: function(){
		var form = Ext.getCmp('form');
		var s = '';
		form.getForm().getFields().each(function (item,index, length){
			if(item.logic!='ignore'){
				var value = item.value == null ? "" : item.value;
				if(item.xtype == 'htmleditor') {
					value  = item.getValue();
				}
				item.originalValue = item.originalValue == null ? "" : item.originalValue;

				if(Ext.typeOf(item.originalValue) != 'object'){


					if(item.originalValue.toString() != value.toString()){//isDirty、wasDirty、dirty一直都是true，没办法判断，所以直接用item.originalValue,原理是一样的
						var label = item.fieldLabel || item.ownerCt.fieldLabel ||
						item.boxLabel || item.ownerCt.title;//针对fieldContainer、radio、fieldset等
						if(label){
							s = s + '&nbsp;' + label.replace(/&nbsp;/g,'');
						}
					}

				}
			}
		});
		return (s == '') ? s : ('表单字段(<font color=green>'+s+'</font>)已修改');
	},
	ResSubmitAudit:function(id){
		var me = this;
		var form = Ext.getCmp('form');			
		Ext.Ajax.request({
	   		url : basePath + form.resSubmitAuditUrl,
	   		params: {
	   			id: id
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){    			
	   					window.location.reload();    				
	   			} 
	   			if (localJson.exceptionInfo) {
						showError(localJson.exceptionInfo);
				}
	   		}
		});
	}
});