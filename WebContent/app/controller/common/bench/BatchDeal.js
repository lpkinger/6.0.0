Ext.QuickTips.init();
Ext.define('erp.controller.common.bench.BatchDeal', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:[
     		'common.bench.BatchDeal','common.bench.BatchDealFormPanel','common.bench.BatchDealGridPanel','core.trigger.AddDbfindTrigger','core.button.CheckCustomerUU',
     		'core.trigger.DbfindTrigger','core.form.FtField','core.form.FtFindField','core.form.ConDateField','core.button.TurnMeetingButton',
     		'core.trigger.TextAreaTrigger','core.form.YnField', 'core.form.MonthDateField','core.form.ConMonthDateField','core.trigger.SchedulerTrigger',
     		'core.grid.YnColumn','core.form.DateHourMinuteField','core.form.SeparNumber','core.grid.YnColumnNV','core.button.DeblockSplit'	
     		],
    init:function(){
    	var me = this;
    	me.resized = false;
    	this.control({
    		'erpBatchDealFormPanel': {
    			alladded: function(form){
    				var grid = Ext.getCmp('batchDealGridPanel');
    				me.resize(form, grid);
    				/**
    				 * 手动加锁
    				 */
    				var sacode = getUrlParam("sacode");
    				var prodcode = getUrlParam("prodcode"),detno = getUrlParam("detno"),ob_noallqty=getUrlParam("ob_noallqty"),prodname=getUrlParam("prodname");
    				if(caller=="HandLocked!Deal"&&sacode&&prodcode){
    					var prodcode_ = Ext.getCmp("prodcode_"),code_ = Ext.getCmp("code_"),pr_name = Ext.getCmp("pr_name"),qty=Ext.getCmp("qty"),pd_detno=Ext.getCmp("pd_detno");
    					prodcode_.setValue(prodcode);
    					code_.setValue(sacode);
    					pr_name.setValue(prodname);
    					qty.setValue(ob_noallqty);
    					pd_detno.setValue(detno);
    				}
    				var items = form.items.items;
					Ext.each(items, function() {
						var val = getUrlParam(this.name);
						if(!Ext.isEmpty(val)) {
							this.setValue(val);
							if(this.xtype == 'dbfindtrigger') {
								this.autoDbfind('form', caller, this.name, this.name + " like '%" + val + "%'");
							}
						}
					});
					if(form.source=='allnavigation'){
        				Ext.each(form.dockedItems.items[0].items.items,function(btn){
        					btn.setDisabled(true);
        				});
        			}
    			}  			
    		},
    		'erpBatchDealGridPanel': {
    			afterrender: function(grid){
    				var form = Ext.getCmp('dealform');
    				me.resize(form, grid);
    				grid.store.on('datachanged', function(store){//dataChanged事件
						me.getProductWh(grid);
					});
    				if(caller == 'ARBill!ToBillOut!Deal'||caller == 'APBill!ToBillOutAP!Deal'){
        				grid.plugins[0].on('afteredit',function(){
        					me.countAmount(grid);
        					
        				});
        				grid.on('selectionchange',function(){
        					me.countAmount(grid);
        				});
    				}
    			},
    			edit:function(ed,d){
    				if(caller == "UpdateMakeSubMaterial" && d.field=='mp_canuseqty'){
	    				//发送请求更新可替代数
	    			  me.updateMakeSub(d);
    				}   				
    			},
    			itemclick: function(selModel, record, item, index, event){//grid行选择
    				if(event.target.getAttribute('class')!='x-grid-row-checker'){
    					if(caller == 'Make!Cost!Deal'){
        			    	url = 'jsps/common/batchDeal.jsp?whoami=Make!OnCost!Deal';
        					if(record) {
        						url += '&ma_code=' + record.data.cd_makecode;
        						url += '&cd_yearmonth=' + record.data.cd_yearmonth;
        						url += '&ma_tasktype=' + record.data.cd_maketype;
        					}
        					me.FormUtil.onAdd('addCostDetailMateria', '月结表', url);
        				}
    				}
    		    }
    		},
    		'field[name=differ]': {
				change: function(field){
					var grid = Ext.getCmp('batchDealGridPanel');
					me.countAmount(grid);
				}
    		},
    		'erpVastDealButton': {
    			click: {
    				fn: function(btn){
	    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
	    			},
	    			lock: 2000
    			}
    		},
    		'erpVastAnalyseButton': {
    			click: function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'erpVastPrintButton': {
    			click: function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'erpVastAllotButton':{
    			click:function(btn){
    				me.vastDeal(btn.ownerCt.ownerCt.dealUrl);
    			}
    		},
    		'erpSaveCostDetailButton':{
    			click:function(btn){
    				me.vastDeal('cost/vastSaveCostDetail.action');
    			}
    		},
    		'erpDifferVoucherCreditButton':{
    			click:function(btn){
    				me.vastDeal('cost/vastDifferVoucherCredit.action');
    			}
    		},
    		'erpNowhVoucherCreditButton':{
    			click:function(btn){
    				me.vastDeal('cost/vastNowhVoucherCredit.action');
    			}
    		},
    		'SchedulerTrigger':{
				afterrender:function(trigger){					
					trigger.setFields=[{field:'va_vecard',mappingfield:'ID'},{field:'va_driver',mappingfield:'VA_DRIVER'}];
				}
			},
    		'erpEndCRMButton':{
    			click:function(btn){
    				me.vastDeal('crm/chanceTurnEnd.action');
    			}
    		},
    		'erpRefreshQtyButton': {
				click : function() {
					this.refreshQty(caller);
				}
			}
    	});
    },
    resize: function(form, grid){
    	if(!this.resized && form && grid && form.items.items.length > 0){
    		var height = window.innerHeight, 
				fh = form.getEl().down('.x-panel-body>.x-column-inner').getHeight();
			form.setHeight(35 + fh);
			grid.setHeight(height - fh - 35);
			this.resized = true;
		}
    },
    vastDeal: function(url){
    	var me = this, grid = Ext.getCmp('batchDealGridPanel');
        var items = grid.getMultiSelected();
        Ext.each(items, function(item, index){
        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
        		item.index = this.data[grid.keyField];
        		grid.multiselected.push(item);        		
        	}
        });
        if(caller=='Make!ToQuaCheck!Deal'){
        	var bool = false;
        	var bool2 = false;
        	var ma_whcode = Ext.getCmp('ma_whcode');
        	if(ma_whcode&&ma_whcode.value==''){
        		showError('仓库编码必填');
        		return;
        	}
        	Ext.each(grid.multiselected, function(item, index){
        		rn = this.data['RN'];
	        	if(this.data['ma_thisqty'] == null || this.data['ma_thisqty'] == ''
	        		|| this.data['ma_thisqty'] == '0' || this.data['ma_thisqty'] == 0){
	        		bool = true;
	        		return false;
	        	}else if(this.data['ma_zldcode']!==undefined&&(this.data['ma_zldcode']==''||this.data['ma_zldcode']==null)){
	        		bool2 = true;
	        		return false;
	        	}
	        });
        	if(bool){
        		showError('本次数量必须大于0,行号：'+rn);
        		return false;
        	}else if(bool2){
        		showError('序列号必填,行号：'+rn);
        		return false;
        	}
        }
    	var form = Ext.getCmp('dealform');
		var records = Ext.Array.unique(grid.multiselected);
		if(records.length > 0){
			if(contains(url,'common/form/vastPost.action',true) || contains(url,'common/vastPostProcess.action',true)) {//流程批量抛转
				this.vastPost(grid, records, url);
				return;
			}
			var params = new Object();
			params.id=new Array();
			params.caller = caller;
			var data = new Array();
			var bool = false;
			Ext.each(records, function(record, index){
				var f = form.fo_detailMainKeyField;
				if((grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
	        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0) 
	        		||(f && this.data[f] != null && this.data[f] != ''
		        		&& this.data[f] != '0' && this.data[f] != 0)){
					bool = true;
					var o = new Object();
					if(grid.keyField){
						o[grid.keyField] = record.data[grid.keyField];
					} else {
						params.id[index] = record.data[form.fo_detailMainKeyField];
					}
					if(grid.toField){
						Ext.each(grid.toField, function(f, index){
							var v = Ext.getCmp(f).value;
							if(v != null && v.toString().trim() != '' && v.toString().trim() != 'null'){
								if(Ext.isDate(v)){
									v = Ext.Date.toString(v);
								}
								o[f] = v;
							} else {
								o[f] = '';
							}
						});
					}
					if(grid.necessaryFields){
						Ext.each(grid.necessaryFields, function(f, index){
							var v = record.data[f];
							if(Ext.isDate(v)){
								v = Ext.Date.toString(v);
							}
							if(Ext.isNumber(v)){
								v = (v).toString();
							}
							o[f] = v;
						});
					}
					data.push(o);
				}
			});
			if(bool && !me.dealing){
				params.data = unescape(Ext.JSON.encode(data).replace(/\\/g,"%"));
				me.dealing = true;
				/*var main = parent.Ext.getCmp("content-panel");
				if(!main){
					main = parent.parent.parent.Ext.getCmp("content-panel")
				}*/
				me.FormUtil.setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath + url,
			   		params: params,
			   		method : 'post',
			   		timeout: 6000000,
			   		callback : function(options,success,response){
			   			me.FormUtil.setLoading(false);
			   			me.dealing = false;
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				var str = localJson.exceptionInfo;			   				
			   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){			   					
			   					str = str.replace('AFTERSUCCESS', '');	
			   					grid.multiselected = new Array();
			   					//获取异常前的top
			    				var oldTop = grid.body.dom.style.top;
			   					grid.getGridColumnsAndStore(grid,null,true);
			   					//改变当前的top
			   					grid.body.dom.style.top = oldTop;
			   				}
			   				showError(str);return;
			   			}
		    			if(localJson.success){
		    				if(localJson.log){
		    					showMessage("提示", localJson.log);
		    				}else{
		    					showMessage("提示", '操作成功！');
		    				}
		    				grid.multiselected = new Array();
		    				//获取异常前的top
		    				var oldTop = grid.body.dom.style.top;
		   					grid.getGridColumnsAndStore(grid,null,true);
		   					//改变当前的top
		   					grid.body.dom.style.top = oldTop;
			   			}
			   		}
				});
			} else {
				showError("没有需要处理的数据!");
			}
		} else {
			showError("请勾选需要的明细!");
		}
    },
    vastPost: function(grid, records, url) {
    	var me = this, win = Ext.getCmp('win-post');
    	grid._postrecords = records;
    	if(!win) {
    		win = Ext.create('Ext.Window', {
    			id: 'win-post',
    			width: '90%',
    			height: '60%',
    			modal: true,
    			layout: 'anchor',
    			items: [{
    				xtype: 'form',
    				anchor: '100% 100%',
    				bodyStyle: 'background: #f1f1f1;',
    				layout: 'column',
    				autoScroll:true,
    				defaults: {
    					xtype: 'checkbox',
    					margin: '2 10 2 10',
    					columnWidth: .33
    				},
    				items: [{
    					xtype: 'displayfield',
    					fieldLabel: '当前账套',
    					id: 'ma_name'    		
    				},{
    					xtype: 'displayfield',
    					fieldLabel: '账套描述',
    					margin: '2 10 30 10',
    					id: 'ma_function',
    					columnWidth: .65
    				},{
    					xtype: 'displayfield',
    					fieldLabel: '目标账套',
    					columnWidth: 1
    				},{
    					boxLabel: '全选',
    					columnWidth: 1,
    					listeners: {
    						change: function(f) {
    		    				var form = f.up('form');
    		    				form.getForm().getFields().each(function(a){
    		    					if(a.xtype == 'checkbox' && a.id != f.id) {
    		    						a.setValue(f.value);
    		    					}
    		    				});
    		    			}
    					}
    				}]
    			}],
    			buttonAlign: 'center',
    			buttons: [{
					text: $I18N.common.button.erpConfirmButton,
					cls: 'x-btn-blue',
					handler: function(btn) {
						var w = btn.ownerCt.ownerCt, form = w.down('form'),
							from = form.down('#ma_name').value,
							items = form.query('checkbox[checked=true]'),
							data = new Array();
						Ext.each(items, function(item){
							if (item.ma_name)
								data.push(item.ma_name);
						});
						if(data.length > 0)
							me.post(w, grid, url, from, data.join(','));
					}
				},{
					text: $I18N.common.button.erpCloseButton,
					cls: 'x-btn-blue',
					handler: function(btn) {
						btn.ownerCt.ownerCt.hide();
					}
				}]
    		});
    		if(contains(url,'?_out=1',true)) this.getOutMasters(win);
    		else this.getMasters(win);
    	}
    	win.show();
    },
    post: function(w, grid, url, from, to) {
    	var records = grid._postrecords;
    	w.setLoading(true);
		var d = new Array(), f = grid.keyField;
		Ext.each(records, function(r) {
			d.push(r.get(f));
		});
		Ext.Ajax.request({
			url: basePath + url,
			params: {
				caller: caller,
				data: d.join(','),
				to: to
			},
			callback: function(opt, s, r) {
				w.setLoading(false);
				if(s) {
					var rs = Ext.decode(r.responseText);
					if(rs.data) {
						showMessage('提示', rs.data);
					} else {
						alert('抛转成功!');
					}
					grid.multiselected = new Array();
					grid._postrecords = null;
   					//获取异常前的top
    				var oldTop = grid.body.dom.style.top;
   					grid.getGridColumnsAndStore(grid,null,true);
   					//改变当前的top
   					grid.body.dom.style.top = oldTop;
   					w.hide();
				}
			}
		});
    },
	/**
	 * 加载系统所有账套
	 */
	getMasters: function(win){
		Ext.Ajax.request({
			url: basePath + 'common/getAbleMasters.action',
			method: 'get',
			callback: function(opt, s, res){
				var r = Ext.decode(res.responseText), c = r.currentMaster;
				if(r.masters){
					var form = win.down('form'), items = new Array();
    				for(var i in r.masters) {
    					var d = r.masters[i];
    					if(d.ma_name != c) {
    						if(d.ma_type == 3) {
    							var o = {boxLabel: d.ma_name + '(' + d.ma_function + ')', ma_name: d.ma_name};
            					items.push(o);
    						}
    					} else {
    						form.down('#ma_name').setValue(c);
    						form.down('#ma_function').setValue(d.ma_function);
    					}
    				}
    				form.add(items);
				}
			}
		});
	},
	getOutMasters:function(win){
		Ext.Ajax.request({
			url: basePath + 'common/getOutMasters.action',
			method: 'get',
			callback: function(opt, s, res){
				var r = Ext.decode(res.responseText), c = r.currentMaster;	
				if(r.data){
					var form = win.down('form'), items = new Array();
					Ext.Array.each(r.data,function(d){
						var o = {boxLabel: d.MO_LOCATION + '(' + d.MO_NAME + ')', ma_name: d.MO_LOCATION};
    					items.push(o);
					});
    				form.down('#ma_name').setValue(c);
    				form.add(items);
				}
			
			}
		});
	},
	refreshQty : function(cal) {
		var tab = null;
		switch(cal) {
			case 'Purchase!ToCheckAccept!Deal' :
				tab = 'Purchase';
				break;
			case 'Purchase!ToNotify!Deal' :
				tab = 'Purchase';
				break;
			case 'Sale!ToAccept!Deal':
				tab = 'Sale';
				break;
			case 'SendNotify!ToProdIN!Deal':
				tab = 'SendNotify';
				break;
		}
		var form = Ext.getCmp('dealform');
		form.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'common/resetqty.action',
			params : {
				tab : tab
			},
			callback : function(opt, s, res) {
				form.setLoading(false);
				var r = Ext.decode(res.responseText);
				if (r.success) {
					alert('已转数量重置成功!');
				}
			}
		});
	},
	getProductWh: function(grid) {
		var prodfield = grid.getProdField();
		if(prodfield) {
			var codes = [];
			grid.store.each(function(d){
				codes.push("'" + d.get(prodfield) + "'");
			});
			Ext.Ajax.request({
				url: basePath + 'scm/product/getProductwh.action',
				params: {
					codes: codes.join(',')
				},
				callback: function (opt, s, r) {
					if(s) {
						var rs = Ext.decode(r.responseText);
						if(rs.data) {
							grid.productwh = rs.data;
						}
					}
				}
			});
		}
	},
    countAmount: function(grid){
    	var me = this;
    	var items = grid.selModel.selected.items;
    	var countamount=0;
    	var taxsum = 0,
    	    differ = Ext.getCmp('differ');
    		priceFormat = grid.down('gridcolumn[dataIndex=abd_thisvoprice]').format,
    		fsize = (priceFormat && priceFormat.indexOf('.') > -1) ? 
    				priceFormat.substr(priceFormat.indexOf('.') + 1).length : 6;
    	Ext.each(items,function(item,index){
    		var a = Number(item.data['abd_thisvoprice']);
    		var b = Number(item.data['abd_thisvoqty']);
    		var rate = Number(item.data['abd_taxrate']);

    		countamount = countamount + Number(grid.BaseUtil.numberFormat(a*b,2));
    		taxsum = taxsum + Number(grid.BaseUtil.numberFormat((a*b*rate/100)/(1+rate/100),2));
    	});
    	//金额合计   不能填写  自动显示所选数据条目的本次发票数*本次发票单价 的总和
       	Ext.getCmp('pi_amounttotal').setValue(Ext.util.Format.number(countamount, "0.00"));
       	if(differ && !Ext.isEmpty(differ.value)){
       		Ext.getCmp('taxsum').setValue(Ext.util.Format.number(taxsum+differ.value, "0.00"));
       	} else {
       		Ext.getCmp('taxsum').setValue(Ext.util.Format.number(taxsum, "0.00"));
       	}
    },
	updateMakeSub:function(d){
		if(d.record.dirty){
			if(Ext.isNumber(d.value) && (d.value==0 ||d.value>0)){
				Ext.Ajax.request({
			   		url : basePath + "pm/make/updateMakeSubMaterial.action",
			   		params: {
			   			data:unescape(escape(Ext.JSON.encode(d.record.data))),
			   			caller:caller
			   		},
			   		method : 'post',
			   		callback : function(options,success,response){
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				d.record.set('mp_canuseqty',d.originalValue);
			   				showError(localJson.exceptionInfo);
			   			}
		    			if(localJson.success){
		    				d.record.commit();
		    				showMessage("提示", "修改锁库数量成功");
			   			}
			   		}
				}); 
			}else{
				d.record.set('mp_canuseqty',d.originalValue);
			}
		}
	}
});