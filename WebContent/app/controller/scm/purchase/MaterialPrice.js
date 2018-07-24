Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.MaterialPrice', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.purchase.MaterialPrice','core.grid.Panel2','core.toolbar.Toolbar','core.button.Appstatus','core.button.ResAppstatus',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
				'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ImportExcel',
				'core.button.ResSubmit','core.button.Banned','core.button.ResBanned','core.button.Abate','core.button.ResAbate',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.FileField','core.button.Sync',
			'core.button.CopyAll','core.grid.detailAttach','core.button.SyncSpecial'],
    init:function(){
    	var me = this;
    	me.alloweditor = true;
    	this.control({
    		'erpGridPanel2': {
    			itemclick: function(view,record){
    				me.itemclick(view,record);
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				//供应商必填
    				var grid = Ext.getCmp('grid'),items = grid.store.data.items,
    					kind = Ext.getCmp('pp_kind').value,
    					statdate = Ext.getCmp('pp_fromdate').value,
    					enddate = Ext.getCmp('pp_todate').value;
    				if(!Ext.isEmpty(enddate)){
    					if(Ext.Date.format(statdate,'Y-m-d') > Ext.Date.format(enddate,'Y-m-d')){
        					showError('有效截止日期不能小于有效开始日期!');return;
        				}
        				if(Ext.Date.format(enddate,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
        					showError('有效截止日期不能小于当前日期!');return;
        				}
    				}
    				var pricebool = me.getSetting('PurchasePrice', 'purcRatePrice');
    				Ext.each(items, function(item){
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(item.data['ppd_currency'] == null || item.data['ppd_currency'] == ''){
    							showError('明细表第' + item.data['ppd_detno'] + '行的币别为空');return;
    						}
    						if(kind == '模材'){
    							console.log(item.data['ppd_prodcode']);
    							if(item.data['ppd_prodcode'] == null || item.data['ppd_prodcode'] == ''){
        							item.set('ppd_prodcode','1');
        							console.log(item.data['ppd_prodcode']);
        						}
    							if(item.data['ppd_price'] == null || item.data['ppd_price'] == '' || 
        								item.data['ppd_price'] == '0' || item.data['ppd_price'] == 0){
    								if(!pricebool){
            							showError('明细表第' + item.data['ppd_detno'] + '行的单价为空或0');return;
    								}
        						}
    						}
    						if(item.data['ppd_vendcode'] == null || item.data['ppd_vendcode'] == ''){
    							showError('明细表第' + item.data['ppd_detno'] + '行的供应商为空');return;
    						}
    						if(item.data['ppd_fromdate'] ==null || item.data['ppd_fromdate'] == ''){
    							item.set('ppd_fromdate', statdate);
    						}
    						if(!Ext.isEmpty(enddate)){
	    						if(item.data['ppd_todate'] ==null || item.data['ppd_todate'] == ''){
	    							item.set('ppd_todate', enddate);
	    						}
    						}
    					}
    				});
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('pp_id').value);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid'),items = grid.store.data.items,
    					kind = Ext.getCmp('pp_kind').value,
    					statdate = Ext.getCmp('pp_fromdate').value,
    					enddate = Ext.getCmp('pp_todate').value;
    				if(!Ext.isEmpty(enddate)){
	    				if(Ext.Date.format(statdate,'Y-m-d') > Ext.Date.format(enddate,'Y-m-d')){
	    					showError('有效截止日期不能小于有效开始日期!');return;
	    				}
	    				if(Ext.Date.format(enddate,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
	    					showError('有效截止日期不能小于当前日期!');return;
	    				}
    				}
    				var pricebool = me.getSetting('PurchasePrice', 'purcRatePrice');
    				Ext.each(items, function(item){
    					if(item.dirty && item.data[grid.necessaryField] != null && item.data[grid.necessaryField] != ""){
    						if(Ext.isEmpty(item.data['ppd_fromdate'])){
    							item.set('ppd_fromdate', statdate);
    						}
    						if(!Ext.isEmpty(enddate)){
	    						if(Ext.isEmpty(item.data['ppd_todate'])){
	    							item.set('ppd_todate', enddate);
	    						}
    						}
    						if(item.data['ppd_currency'] == null || item.data['ppd_currency'] == ''){
    							showError('明细表第' + item.data['ppd_detno'] + '行的币别为空');return;
    						}
    						if(kind == '模材'){
    							if(item.data['ppd_prodcode'] == null || item.data['ppd_prodcode'] == ''){
        							item.set('ppd_prodcode','1');
        						}
	    						if(item.data['ppd_price'] == null || item.data['ppd_price'] == '' || 
	    								item.data['ppd_price'] == '0' || item.data['ppd_price'] == 0){
	    							if(!pricebool){
            							showError('明细表第' + item.data['ppd_detno'] + '行的单价为空或0');return;
    								}
	    						}
    						}
    						if(item.data['ppd_vendcode'] == null || item.data['ppd_vendcode'] == ''){
    							showError('明细表第' + item.data['ppd_detno'] + '行的供应商为空');return;
    						}
    					}
    				});
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addMaterialPrice', '新增物料核价', 'jsps/scm/purchase/materialPrice.jsp');
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pp_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var bool = true;
    				var statdate = Ext.getCmp('pp_fromdate').value,
    					enddate = Ext.getCmp('pp_todate').value;
    				if(!Ext.isEmpty(enddate)){
	    				if(Ext.Date.format(statdate,'Y-m-d') > Ext.Date.format(enddate,'Y-m-d')){
	    					bool=false;
	    					showError('有效截止日期小不能于有效开始日期!');return;
	    				}
	    				if(Ext.Date.format(enddate,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
	    					bool=false;
	    					showError('有效截止日期不能小于当前日期!');return;
	    				}
    				}
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				Ext.Array.each(items, function(item){
    					if(!Ext.isEmpty(item.data['ppd_prodcode'])){
    						if(Ext.isEmpty(item.data['ppd_fromdate'])){
    							item.set('ppd_fromdate', statdate);
    						}
    						if(!Ext.isEmpty(enddate)){
	    						if(Ext.isEmpty(item.data['ppd_todate'])){
	    							item.set('ppd_todate', enddate);
	    						}
	    						if(Ext.Date.format(item.data['ppd_todate'],'Y-m-d') < Ext.Date.format(item.data['ppd_fromdate'],'Y-m-d')){
	    							bool=false;
	    							showError('明细行第'+item.data['ppd_detno']+'行有效期止不能小于有效期始!');
	    							return ;
	    						}
    						}
    					}
    				});
    				if(bool){
    					me.FormUtil.onSubmit(Ext.getCmp('pp_id').value);
    				}
    			}
    		},
    		 'erpImportExcelButton':{
    			   afterrender:function(btn){
    				   var statuscode=Ext.getCmp('pp_statuscode').getValue();
    				   if(statuscode&&statuscode!='ENTERING'){
    					   btn.hide();
    				   }
    			   }  
    		   },
    		 'filefield[id=excelfile]':{
  			   change: function(field){
  			    	if(contains(field.value, "\\", true)){
  			    		filename = field.value.substring(field.value.lastIndexOf('\\') + 1);
  			    	} else {
  			    		filename = field.value.substring(field.value.lastIndexOf('/') + 1);
  			    	}
  					field.ownerCt.getForm().submit({
  	            	    url: basePath + 'common/upload.action?em_code=' + em_code,
  	            		waitMsg: "正在解析文件信息",
  	            		success: function(fp,o){
  	            			if(o.result.error){
  	            				showError(o.result.error);
  	            			} else {	            				
  	            				var filePath=o.result.filepath;	
  	            				var keyValue=Ext.getCmp('pp_id').getValue();
  	            				Ext.Ajax.request({//拿到form的items
  	            		        	url : basePath + 'scm/PurchasePrice/ImportExcel.action',
  	            		        	params:{
  		            					  id:keyValue,
  		            					  fileId:filePath
  		            				  },
  	            		        	method : 'post',
  	            		        	callback : function(options,success,response){
  	            		        		var result=Ext.decode(response.responseText);
  	            		        		if(result.success){
  	            		        			var grid=Ext.getCmp('grid');
  	            		        			var param={
  	            		        				caller:'PurchasePrice',
  	            		        				condition:'ppd_ppid='+keyValue
  	            		        			};
  	            		        			grid.GridUtil.loadNewStore(grid,param);
  	            		        		}else{
  	            		        			if(result.exceptionInfo != null){
  	            		            			showError(res.exceptionInfo);return;
  	            		            		}
  	            		        		}
  	            		        	}
  	            				});	            				
  	            			}
  	            		}	
  	            	});
  				}
  		   },
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pp_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('pp_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('pp_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('pp_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				/*var status = Ext.getCmp('pp_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}*/
    				btn.hide();
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('pp_id').value);
    			}
    		},
    		'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('pp_id').value);
    			}
    		},
    		'erpResAbateButton':{//转有效
    			afterrender: function(btn){
    				Ext.getCmp('erpResAbateButton').setDisabled(true);
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'),
    					record = grid.selModel.lastSelected;
    				if(!record) {
    					return;
    				}
    				var ppdid = record.get('ppd_id');
    				if(ppdid == null || ppdid == 0) {
    					return;
    				}
	    			Ext.Ajax.request({
	        			url : basePath + "scm/purchase/resabatepurchaseprice.action?caller=MaterialPrice",
	        			params:{
	        				id: ppdid
	        			},
	        			method:'post',
	        			callback:function(options,success,response){
	        				var localJson = new Ext.decode(response.responseText);
	            			if(localJson.success){
	            				Ext.Msg.alert("提示","操作成功！");
	            				window.location.reload();
	            			} else {
	            				if(localJson.exceptionInfo){
	            	   				var str = localJson.exceptionInfo;
	            	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	            	   					str = str.replace('AFTERSUCCESS', '');
	            	   					showError(str);
	            	   				} else {
	            	   					showError(str);return;
	            	   				}
	            	   			}
	            			}
	        			}
	        		});
	    		}
    		},
    		'erpAbateButton':{//失效
    			afterrender: function(btn){
    				Ext.getCmp('erpAbateButton').setDisabled(true);
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'),
    					record = grid.selModel.lastSelected;
    				if(!record) {
    					return;
    				}
    				var ppdid = record.get('ppd_id');
    				if(ppdid == null || ppdid == 0) {
    					return;
    				}
	    			Ext.Ajax.request({
	        			url : basePath + "scm/purchase/abatepurchaseprice.action?caller=MaterialPrice",
	        			params:{
	        				id: ppdid
	        			},
	        			method:'post',
	        			callback:function(options,success,response){
	        				var localJson = new Ext.decode(response.responseText);
	            			if(localJson.success){
	            				Ext.Msg.alert("提示","操作成功！");
	            				window.location.reload();
	            			} else {
	            				if(localJson.exceptionInfo){
	            	   				var str = localJson.exceptionInfo;
	            	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	            	   					str = str.replace('AFTERSUCCESS', '');
	            	   					showError(str);	            	   	
	            	   				} else {
	            	   					showError(str);return;
	            	   				}
	            	   			}
	            			}
	        			}
	        		});
	    		}
    		},
    		'erpCopyButton': {
    			click: function(btn) {
    				me.copy();
    			}
    		},
            'erpSyncButton': {
            	afterrender: function(btn) {
                    var form = btn.ownerCt.ownerCt, s = form.down('#pp_statuscode');
                    if (s.getValue() != 'AUDITED')
                        btn.hide();
                }
            }
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	if(this.alloweditor){
    		this.GridUtil.onGridItemClick(selModel, record);
    	}
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	itemclick:function(view, record){
    	if (Ext.getCmp('fileform')) {
			Ext.getCmp('fileform').setDisabled(false);
		}
		this.GridUtil.onGridItemClick(view, record);
		var ppdid = record.get('ppd_id');
		if(ppdid != null && ppdid != 0) {
			if(record.data['ppd_status']=='有效'){
				Ext.getCmp('erpAbateButton').setDisabled(false);
				Ext.getCmp('erpResAbateButton').setDisabled(true);
			}else{
				Ext.getCmp('erpAbateButton').setDisabled(true);
				Ext.getCmp('erpResAbateButton').setDisabled(false);
			}
			if(record.data['ppd_appstatus']=='合格'){
				Ext.getCmp('erpAppstatusButton').setDisabled(true);
				Ext.getCmp('erpResAppstatusButton').setDisabled(false);
			}else{
				Ext.getCmp('erpAppstatusButton').setDisabled(false);
				Ext.getCmp('erpResAppstatusButton').setDisabled(true);
			}
		}
    },
	/**
	 * 复制核价单
	 */
	copy: function(){
		var me = this, form = Ext.getCmp('form');
		var v = form.down('#pp_id').value;
		if(v > 0) {
			form.setLoading(true);
			Ext.Ajax.request({
				url: basePath + 'scm/purchase/copyMaterialPrice.action',
				params: {
					caller : caller,
					id : v
				},
				callback: function(opt, s, r){
					form.setLoading(false);
					var res = Ext.decode(r.responseText);
					if(res.data) {
						var url = 'jsps/scm/purchase/materialPrice.jsp?formCondition=pp_idIS' 
							+ res.data.id + '&gridCondition=ppd_ppidIS' 
							+ res.data.id;
						showMessage('提示', '复制成功', 2000);
						me.FormUtil.onAdd(null, '核价单', url);
					} else {
						showError(res.exceptionInfo);
					}
				}
			});
		}
	},
	getSetting : function(cal, code) {
		var me = this;
		var t = false;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldData.action',
	   		async: false,
	   		params: {
	   			caller: 'configs',
	   			field: 'data',
	   			condition: 'code=\''+code+'\' and caller=\''+cal+'\''
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if(r.exceptionInfo){
	   				showError(r.exceptionInfo);return;
	   			}
    			if(r.success && r.data){
    				if(r.data == '1'){
    					t = true;
    				}
    			}
	   		}
		});
		return t;
	}
});