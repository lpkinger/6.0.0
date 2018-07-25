Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.BatchInquiry', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.purchase.BatchInquiry','core.grid.Panel2','core.toolbar.Toolbar','core.form.MultiField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Print','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail',
  			'core.button.ResSubmit','core.button.Banned','core.button.ResBanned','core.button.Flow',
			'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.form.YnField','core.button.ProductDetail'
      	],
    init:function(){
    	var me = this;
		this.control({
			'#prodtab':{
				beforerender:function(){
					var form = Ext.getCmp('form');
    				var tabid = getUrlParam("tabid");
    				var condition = getUrlParam("formCondition");
    				if(tabid && condition==null){
    					//供应商资源库->发起询价 自动带入数据
    					var tab = parent.Ext.getCmp(tabid);
        				var datas = tab.batchInquirydatas;
        				if(datas){
    					//pr_code -> bip_prodcode 物料编号
        				//en_name -> biv_vendname 供应商名
						//en_uu -> biv_venduu 供应商uu
						//en_currency -> biv_currency 供应商币别 
        					Ext.defer(function(){
		        					var prodtabStore = Ext.getCmp('prodtab').getStore();
		        					var vendtabStore = Ext.getCmp('vendtab').getView().getStore();
		        					var pr_codeArray = datas['pr_code'];
		        					if(pr_codeArray){
		        						pr_codeArray = datas['pr_code'].split(",");
		        						if(pr_codeArray.length>1){
			        						for(var i=0;i<pr_codeArray.length;i++){
			        							if(i==0){
			        								prodtabStore.loadData([{
			        									"bip_prodcode":pr_codeArray[i]
			        								}]);
			        							}else{
			        								prodtabStore.loadData([{
			        									"bip_prodcode":pr_codeArray[i]
			        								}],true);
			        							}
			        							prodtabStore.data.items[i].dirty = true;
			        						}
			        					}else{
			        						prodtabStore.loadData([{
			        							"bip_prodcode":datas['pr_code']
			        						}]);
			        						prodtabStore.data.items[0].dirty = true;
			        					}
		        					}
		        					
		        					var p = [{
		        						"biv_vendname" : datas['en_name'],
			        					"biv_venduu" : datas['en_uu'],
			        					"biv_currency" : datas['en_currency']
		        					}];
	            					vendtabStore.loadData(p);
		        					vendtabStore.data.items[0].dirty = true;
		        					Ext.getCmp('tab').doLayout();
		    					},500);
        				}
				}
				}
			},
			'erpGridPanel2': {
    			itemclick: this.onGridItemClick
    		},
			'erpSaveButton': {
				click: function(btn){
					var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
					me.beforeSave();
				}
			},
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('bi_id').value);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.beforeSave(true);
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpProductDetailButton':{
				afterrender: function(btn){
					var status = Ext.getCmp('bi_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
                click: function(){
                    var bi_code = Ext.getCmp('bi_code').value;
                    var record = Ext.getCmp('prodtab').selModel.lastSelected;
                    if(record){
                        var c = record.data['bip_prodcode'];
                        var url='jsps/common/datalist.jsp?whoami=ProdDetail&urlcondition=id_prodcode=\''+c+'\' and in_code=\''+bi_code+'\'';
                    	Ext.create('Ext.window.Window',{
                    		title: '<span style="color:#CD6839;">具体报价</span>',
                    		iconCls: 'x-button-icon-set',
                    		closeAction: 'destory',
                    		height: "100%",
                    		width: "80%",
                    		maximizable : true,
                    		buttonAlign : 'center',
                    		layout : 'fit',
                    		items : [{    
                    			header:false, 
                    			html : '<iframe src="'+basePath+url+'"  id="setframe" name="setframe" width="100%" height="100%"></iframe>', 
                    			border:false 
                    		}]
                    	}).show();
                    } else {
                        Ext.Msg.alert("提示","请先选择明细!");
                    }
                }
            },
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('bi_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('bi_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('bi_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('bi_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('bi_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					var me = this;
					var form = Ext.getCmp('form');
					var id = Ext.getCmp('bi_id').value;
					me.setLoading(true);
					Ext.Ajax.request({
						url : basePath + form.auditUrl,
						params: {
							id: id
						},
						method : 'post',
						callback : function(options,success,response){
							me.setLoading(false);
							var localJson = new Ext.decode(response.responseText);
							if(localJson.success){
								if(localJson.log){
			    					showMessage("提示", localJson.log);
			    				}
								window.location.reload();
							} else {
								if(localJson.exceptionInfo){
									var str = localJson.exceptionInfo;
									if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
										str = str.replace('AFTERSUCCESS', '');
										showMessage("提示", str);
										auditSuccess(function(){
											window.location.reload();
										});
									} else {
										showError(str);return;
									}
								}
							}
						}
					});
				}
			},
			'combo[name=bi_kind]':{
				change: function(self,newVal,oldVal){
					if(newVal != '批量询价'){
						Ext.getCmp('vendtab').tab.hide();
	        			var tab = Ext.getCmp('tab');
	        			if(tab.getActiveTab().id!='prodtab'){
	        				tab.setActiveTab('prodtab');
	        			}
					}else if (newVal == '批量询价'){
						Ext.getCmp('vendtab').tab.show();
	        			Ext.getCmp('tab').setActiveTab('vendtab');
					}
				},
				afterrender: function(){
    				var tabid = getUrlParam("tabid");
    				var condition = getUrlParam("formCondition");
    				if(tabid && condition==null){
    					//供应商资源库->发起询价 自动带入数据
    					var bi_kind = Ext.getCmp('bi_kind');
    					if(bi_kind){
    						bi_kind.setValue("批量询价");
    					}
    				}
    				var kind = Ext.getCmp('bi_kind').value;
					if(kind != '批量询价'){
						Ext.getCmp('vendtab').tab.hide();
					}
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('bi_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('bi_id').value);
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
	beforeSave:function(isUpdate){
		var me = this;
		var form = Ext.getCmp('form');
		if(!me.FormUtil.checkForm()){
			return;
		}
		var end = Ext.getCmp('bi_enddate').value;
		var kind = Ext.getCmp('bi_kind').value;
		if(!Ext.isEmpty(end)){
			if(Ext.Date.format(end,'Y-m-d') < Ext.Date.format(new Date(),'Y-m-d')){
				bool=false;
				showError('有效期小于当前日期，请修改报价截止日期!');return;
			}
		}
		if(isUpdate){
			if(form.codeField && (Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == '')){
				showError('编号不能为空.');
				return;
			}
		}else{
			if(form.keyField && (Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == '')){
				me.FormUtil.getSeqId(form);
			}
		}
		if(isUpdate){
			var s1 = me.FormUtil.checkFormDirty(form);
			var s2 = '';
			var grids = Ext.ComponentQuery.query('gridpanel');
			if(grids.length > 0 && !grids[0].ignore){//check所有grid是否已修改
				Ext.each(grids, function(grid, index){
					if(me.GridUtil){
						var msg = me.GridUtil.checkGridDirty(grid);
						if(msg.length > 0){
							s2 = s2 + '<br/>' + msg;
						}
					}
				});
			}
			if(s1 == '' && (s2 == '' || s2 == '<br/>')){
				showError('还未添加或修改数据.');
				return;
			}
		}
		
		var grid1 = Ext.getCmp('prodtab');
		var grid2 = Ext.getCmp('vendtab');	
		var prodcount=grid1.getStore().getCount();
		var vendcount=grid2.getStore().getCount();
		var proditems = grid1.store.data.items;
		var venditems = grid2.store.data.items;
		for(var i=0;i<prodcount;i++){
			for(var j=0;j<prodcount;j++){
				if(proditems[i].data['bip_prodcode'] == proditems[j].data['bip_prodcode'] &&proditems[i].data['bip_detno'] != proditems[j].data['bip_detno']){
					showError('明细行存在相同物料'+proditems[i].data['bip_prodcode']+'行号为:'+proditems[i].data['bip_detno']+'与'+proditems[j].data['bip_detno']);
					return;
				}
			}
		}
		for(var i=0;i<vendcount;i++){
			for(var j=0;j<vendcount;j++){
				if(venditems[i].data['biv_vendcode'] == venditems[j].data['biv_vendcode'] &&venditems[i].data['biv_detno'] != venditems[j].data['biv_detno']){
					showError('明细行存在相同供应商'+venditems[i].data['biv_vendcode']+'行号为:'+venditems[i].data['biv_detno']+'与'+venditems[j].data['biv_detno']);
					return;
				}
			}
		}
		if(!prodcount>0){
			showError('物料为空，请添加物料再进行操作.');
			return;
		}
		if(!vendcount>0 && kind == '批量询价'){
			showError('供应商为空，请添加供应商再进行操作.');
			return;
		}
		var param1 = new Array();
		if(grid1){
			param1 = me.GridUtil.getGridStore(grid1);
		}
		
		var param2 = new Array();
		if(grid2){
			param2 = me.GridUtil.getGridStore(grid2);
		}
		
		
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			//去除ignore字段
			var keys = Ext.Object.getKeys(r), f;
			var reg = /[!@#$%^&*()'":,\/?]|[\t|\n|\r]/g;
			Ext.each(keys, function(k){
				f = form.down('#' + k);
				if(f && f.logic == 'ignore') {
					delete r[k];
				}
				if(k == form.codeField && !Ext.isEmpty(r[k])) {
					r[k] = r[k].trim().replace(reg, '');
				}
			});
			me.save(r, param1, param2, isUpdate);
		}else{
			me.FormUtil.checkForm();
		}		
	},
	save: function(){
		var me = this;
		var form = Ext.getCmp('form');
		var params = new Object();
		var r = arguments[0],isUpdate = arguments[arguments.length-1];
		Ext.each(Ext.Object.getKeys(r), function(k){//去掉页面非表单定义字段
			if(contains(k, 'ext-', true)){
				delete r[k];
			}
		});	
		params.formStore = unescape(Ext.JSON.encode(r).replace(/\\/g,"%"));
		params.param1 = unescape(arguments[1].toString().replace(/\\/g,"%"));
		params.param2 = unescape(arguments[2].toString().replace(/\\/g,"%"));
		Ext.Ajax.request({
	   		url : basePath + (isUpdate?form.updateUrl:form.saveUrl),
	   		params : params,
	   		method : 'post',
	   		callback : function(options,success,response){	   			
	   			var localJson = new Ext.decode(response.responseText);
    			if(localJson.success){
    				saveSuccess(function(){
    					//add成功后刷新页面进入可编辑的页面 
		   				var value = r[form.keyField];
		   		    	var formCondition = form.keyField + "IS" + value ;
		   		    	if(contains(window.location.href, '?', true)){
			   		    	window.location.href = window.location.href + '&formCondition=' + 
			   					formCondition+'&gridCondition=bip_biidIS'+value;
			   		    } else {
			   		    	window.location.href = window.location.href + '?formCondition=' + 
			   					formCondition+'&gridCondition=bip_biidIS'+value;
			   		    }
    				});
	   			} else if(localJson.exceptionInfo){
	   				var str = localJson.exceptionInfo;
	   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
	   					str = str.replace('AFTERSUCCESS', '');
	   					saveSuccess(function(){
	    					//add成功后刷新页面进入可编辑的页面 
			   				var value = r[form.keyField];
			   		    	var formCondition = form.keyField + "IS" + value ;
			   		    	if(contains(window.location.href, '?', true)){
				   		    	window.location.href = window.location.href + '&formCondition=' + 
				   		    	formCondition+'&gridCondition=bip_biidIS'+value;
				   		    } else {
				   		    	window.location.href = window.location.href + '?formCondition=' + 
				   		    	formCondition+'&gridCondition=bip_biidIS'+value;
				   		    }
	    				});
	   					showError(str);
	   				} else {
	   					showError(str);
		   				return;
	   				}
	   			} else{
	   				saveFailure();//@i18n/i18n.js
	   			}
	   		}
		});
	},
	setLoading : function(b) {// 原this.getActiveTab().setLoading()换成此方法,解决Window模式下无loading问题
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
	},
});