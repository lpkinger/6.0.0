Ext.QuickTips.init();
Ext.define('erp.controller.fa.ars.Voucher', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'fa.ars.Voucher','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'fa.ars.VoucherFlowGrid','fa.ars.DetailAssGrid', 
    		'core.button.CreateTemplate', 'core.button.Add', 'core.button.Save', 'core.button.Close', 'core.button.Source',
    		'core.button.ExportTemplate', 'core.button.Submit', 'core.button.ResSubmit', 'core.button.Audit', 'core.button.ResAudit',
    		'core.button.Update', 'core.button.Delete', 'core.button.DeleteDetail', 'core.button.RushRed',
    		'core.button.VoucherFlow', 'core.button.CopyAll','core.button.ExportExcelButton','core.button.Print',
    		'core.trigger.DbfindTrigger','core.grid.YnColumn', 'core.form.YnField', 'core.trigger.CateTreeDbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpFormPanel': {
    			afterload: function(form) {
    				Ext.defer(function(){// 非手工做的、有来源的凭证，不能改凭证字
    					var lead = Ext.getCmp('vo_lead'), sr = Ext.getCmp('vo_source'),
    						sc = Ext.getCmp('vo_sourcecode'), id = form.down('#vo_id');
    					if(lead && ((sr && !Ext.isEmpty(sr.getValue())) || 
    							(sc && !Ext.isEmpty(sc.getValue())))){
    						lead.setReadOnly(true);
    					}
    					if(id && id.value) {
    						me.loadAdjustData(form);
    					}
    				}, 200);
    			}
    			/*afterrender:function(){
    				Ext.getCmp('Voucher').hide();
    			}*/
    		},
    		'#Voucher': {
    			afterrender: function(btn){
					btn.hide();
    			}   		
    		},
    		'#vo_id': {
    			afterrender: function(f) {
    				me.setVoucherMonth(f.up('form'));
    			},
    			change: function(f){
    				var grid = Ext.getCmp('flowgrid');
    				if(grid) {
    					if(!Ext.isEmpty(f.value) && f.value != 0){
        					grid.getMyData(f.value);
        				} else {
        					if(grid.columns && grid.columns.length > 2){
        						grid.GridUtil.add10EmptyItems(grid);
        					} else {
        						grid.getMyData(-1);
        					}
        				}
    				}
    				var ff = Ext.getCmp('form_disable');
    				ff.FormUtil.loadNewStore(ff, {caller: ff.caller, condition: "vo_id=" + f.value});
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
    				//保存之前的一些前台的逻辑判定
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				Ext.each(items,function(item,index){
    					if(!Ext.isEmpty(item.data['vd_catecode']) || !Ext.isEmpty(item.data['vd_explanation'])){
    						if((Ext.isEmpty(item.data['vd_debit']) || item.data['vd_debit'] == 0) && (Ext.isEmpty(item.data['vd_credit']) || item.data['vd_credit'] == 0)){
    							showError('明细表第' + item.data['vd_detno'] + '行借、贷方金额均未填写!');
    						}
    					}
    				});
    				this.beforeSave();
    			}
    		},
    		//查看来源
    		'erpSourceButton': {
    			afterrender: function(btn){
    				Ext.defer(function(){
	    				var v = Ext.getCmp('vo_source').value;
	    				if(v == null || v == '' || v == '主营业务成本'){
	    					btn.hide();
	    				}
    				}, 200);
    			},
    			click: function(){
    				var id = Ext.getCmp('vo_id').value;
    				if(id != null && id > 0) {
    					me.getSource(id);
    				}
    			}
    		},
    		'erpExportExcelButton':{
    			afterrender:function(btn){
    				//btn.exportCaller="Voucher!DetailAss!Export";
    				btn.exportCaller="Voucher!DetailandAss!Export";
    			    var status = Ext.getCmp('vo_statuscode').value;
    				if(status&&status!='ENTERING'){
    					btn.hide();
    				}
    			}
    		},
    		 'filefield[id=excelfile]':{
  			   change: function(field){
  					warnMsg('确认要重新导入吗?', function(btn){
  						if(btn == 'yes'){
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
  		  	            				var keyValue=Ext.getCmp('vo_id').getValue();
  		  	            				Ext.Ajax.request({//拿到form的items
  		  	            		        	url : basePath + 'fa/ars/ImportExcel.action',  		  	            		  
  		  	            		        	params:{
  		  		            					  id:keyValue,
  		  		            					  fileId:filePath
  		  		            				  },
  		  	            		        	method : 'post',
  		  	            		        	callback : function(options,success,response){
  		  	            		        		var result=Ext.decode(response.responseText);
  		  	            		        		if(result.success){
  		  	            		        			Ext.Msg.alert('提示','导入成功!');
  		  	            		        			window.location.reload();
  		  	            		        		}else{
  		  	            		        			var err = result.exceptionInfo || result.error;
  		  	            		        			if(err != null){
  		  	            		            			showError(err);
  		  	            		            		}
  		  	            		        		}
  		  	            		        	}
  		  	            				});	            				
  		  	            			}
  		  	            		}	
  		  	            	});
  						}
  					});
  			   }
  		   },
    		'erpVoucherFlowButton': {
    			click: function(){
    				this.showVoucherFlow();
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpCopyButton': {
    			click: function(btn) {
    				this.copy();
    			}
    		},
    		'erpRushRedButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('vo_statuscode');
    				if(status && status.value != 'ACCOUNT'){
    					btn.hide();
    				}
    			},
    			click: function(btn) {
    				this.rushRed();
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				Ext.each(items,function(item,index){
    					if(!Ext.isEmpty(item.data['vd_catecode']) || !Ext.isEmpty(item.data['vd_explanation'])){
    						if((Ext.isEmpty(item.data['vd_debit']) || item.data['vd_debit'] == 0) && (Ext.isEmpty(item.data['vd_credit']) || item.data['vd_credit'] == 0)){
    							showError('明细表第' + item.data['vd_detno'] + '行借、贷方金额均未填写!');
    						}
    					}
    				});
    				this.beforeUpdate();
    			}
    		},
    		 'erpPrintButton': {
                click: function(btn) {
                    var reportName = '';
                    reportName = "vouclist_rmb";
                    var condition = '{Voucher.vo_id}=' + Ext.getCmp('vo_id').value + '';
                    var id = Ext.getCmp('vo_id').value;
                    me.FormUtil.onwindowsPrint(id, reportName, condition);
                }
            },
    		'erpDeleteButton': {
    			/*afterrender: function(btn){
    				Ext.defer(function(){
    					var source = Ext.getCmp('vo_source'), 
    						sourceCode = Ext.getCmp('vo_sourcecode');
        				if((source && source.value) || (sourceCode && sourceCode.value)){
        						btn.hide();
        				}
    				}, 200);
    			},*/
    			click: function(btn){
    				var source = Ext.getCmp('vo_source'), 
					sourceCode = Ext.getCmp('vo_sourcecode');   					
					if((source && source.value) || (sourceCode && sourceCode.value)){
						showError('无法删除有来源的凭证！');
						return;
					}
					me.FormUtil.onDelete(Ext.getCmp('vo_id').value);
    			}
    		},
    		'erpAddButton': {
	    		click: function() {
	                me.FormUtil.onAdd('addVoucher', '新增凭证', 'jsps/fa/ars/voucher.jsp');
	            }
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('vo_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var form = Ext.getCmp('form'), adjust = form.down('#vo_adjust'), status = form.down('#vo_statuscode'), ym = form.down('#vo_yearmonth');
    				if (status.value == 'ENTERING') {
    					if(adjust && adjust.value) {
    						if(ym && !ym.value){
    							showError('请先选择期间！');
    							return;
    						}
    					}
    				}
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				Ext.each(items,function(item,index){
    					if(!Ext.isEmpty(item.data['vd_catecode']) || !Ext.isEmpty(item.data['vd_explanation'])){
    						if((Ext.isEmpty(item.data['vd_debit']) || item.data['vd_debit'] == 0) && (Ext.isEmpty(item.data['vd_credit']) || item.data['vd_credit'] == 0)){
    							showError('明细表第' + item.data['vd_detno'] + '行借、贷方金额均未填写!');
    							return;
    						}
    					}
    				});
    				me.FormUtil.onSubmit(Ext.getCmp('vo_id').value, false, this.beforeUpdate, this);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('vo_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('vo_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('vo_statuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				var grid = Ext.getCmp('grid'), items = grid.store.data.items;
    				Ext.each(items,function(item,index){
    					if(!Ext.isEmpty(item.data['vd_catecode']) || !Ext.isEmpty(item.data['vd_explanation'])){
    						if((Ext.isEmpty(item.data['vd_debit']) || item.data['vd_debit'] == 0) && (Ext.isEmpty(item.data['vd_credit']) || item.data['vd_credit'] == 0)){
    							showError('明细表第' + item.data['vd_detno'] + '行借、贷方金额均未填写!');
    							return;
    						}
    					}
    				});
    				me.FormUtil.onAudit(Ext.getCmp('vo_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('vo_statuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('vo_id').value);
    			}
    		},
    		'field[name=vo_errstring]': {
    			afterrender: function(f){
    				if(f.value != null && f.value != ''){
    					f.inputEl.setStyle({color: 'OrangeRed'});
    				} else {
    					f.setValue('正常');
    					f.inputEl.setStyle({color: '#0A85D7'});
    					f.originalValue = f.value;
    				}
    			},
    			change: function(f){
    				if(f.value != null && f.value != ''){
    					f.inputEl.setStyle({color: 'OrangeRed'});
    				} else {
    					f.setValue('正常');
    					f.inputEl.setStyle({color: '#0A85D7'});
    					f.originalValue = f.value;
    				}
    			}
    		},
    		'field[name=vo_date]': {
    			change: function(f){
    				if(f.value == null){
    					f.setValue(new Date());
    				}
    			}
    		},
    		'field[name=vo_yearmonth]':{
    			change: function(f){
    				var date = f.up('form').down('#vo_date'), val = f.value;
    				if (date && val && f.is('combo')) {
    					f.store.each(function(item){
    						if(item.get('value') == val) {
    							date.setValue(item.get('ad_date')); 
    						}
    					});
    					if(f.value.length > 6){
    						Ext.getCmp('vo_adjust').setValue(true);
    					}
    				}
    			}
    		},
    		'checkbox[name=vo_adjust]': {
    			afterrender: function(f){
    				me.BaseUtil.getSetting('sys', 'auditDuring', function(bool) {
    					if(bool) {
    						f.show();
    					} else {
    						f.hide();
    					}
    	            });
    			},
    			change: function(f){
    				me.loadAdjustData(f.up('form'));
    			}
    		},
    		'erpDeleteDetailButton': {
    			afterrender: function(btn){
    				//将当前行的 借方/贷方 以及外币借方/外币贷方 互换
    				btn.ownerCt.add({
    					text: '借贷调换',
    					width: 85,
    					disabled: true,
    			    	cls: 'x-btn-gray',
    			    	id: 'replace'
    				});
    				//当前行的借方 = 其它行的贷方总额-其它行的借方总额
    				btn.ownerCt.add({
    					text: '找平',
    					width: 65,
    					disabled: true,
    			    	cls: 'x-btn-gray',
    			    	id: 'level'
    				});
    			},
    			afterdelete: function(d, r, btn){
    				//更新凭证状态
    				Ext.Ajax.request({
    					url: basePath + 'fa/ars/validVoucher.action',
    					params: {
    						id: d.vd_void
    					},
    					callback: function(opt, s, r){
    						var res = Ext.decode(r.responseText);
    						if(res.success) {
    							var f = Ext.getCmp('vo_errstring');
    							f.setValue((res.errstring || ''));
    							f.dirty = false;
    							f.originalValue = f.value;
    						}
    					}
    				});
    			}
    		},
    		'ExportExcelButton':{
    			afterrender:function(btn){
    				btn.caller='Voucher!DetailAss!Export';
    			}
    			
    		},
    		'erpGridPanel2': {
    			afterrender: function(grid){
    				grid.plugins[0].on('beforeedit', function(args) {
    					var iscashflow = Ext.getCmp('vo_iscashflow');
    					if(iscashflow && iscashflow.value != 0){
    						if (args.field == "vd_flowcode") {
    							var bool = false;
    							if (args.record.get('ca_cashflow') == 0){
                        			bool = true;
                        		}
    						}
    						return bool;
    					} else {
    						if (args.field == "vd_flowcode") {
    							return false;
    						}
    					}
                    	if (args.field == "vd_debit") {
                    		var bool = true;
                    		if (args.record.get('vd_credit') != null && args.record.get('vd_credit') > 0){
                    			bool = false;
                    		}
                    		if (args.record.get('vd_doublecredit') != null && args.record.get('vd_doublecredit') > 0){
                    			bool = false;
                    		}
                    		return bool;
                        }
                        if (args.field == "vd_credit") {
                        	var bool = true;
                        	if (args.record.get('vd_debit') != null && args.record.get('vd_debit') > 0){
                    			bool = false;
                    		}
                    		if (args.record.get('vd_doubledebit') != null && args.record.get('vd_doubledebit') > 0){
                    			bool = false;
                    		}
                    		return bool;
                        }
                        if (args.field == "vd_doubledebit") {
                        	var bool = true;
                        	if (args.record.get('vd_doublecredit') != null && args.record.get('vd_doublecredit') > 0){
                    			bool = false;
                    		}
                    		if (args.record.get('vd_credit') != null && args.record.get('vd_credit') > 0){
                    			bool = false;
                    		}
                    		return bool;
                        }
                        if (args.field == "vd_doublecredit") {
                        	var bool = true;
                        	if (args.record.get('vd_doubledebit') != null && args.record.get('vd_doubledebit') > 0){
                    			bool = false;
                    		}
                    		if (args.record.get('vd_debit') != null && args.record.get('vd_debit') > 0){
                    			bool = false;
                    		}
                    		return bool;
                        }
                    });
    				var f = Ext.getCmp('vo_currencytype');
    				if(f) {
    					Ext.defer( function(){
    						me.changeCurrencyType(f);
    					},200);    					
    				}
    				Ext.defer(function(){
    					Ext.EventManager.addListener(document.body, 'keydown', function(e){
        					if(e.getKey() == 187 && ['vd_debit', 'vd_credit'].indexOf(e.target.name) > -1) {
        						me.levelOut(e.target);
        					}
        				});
    				}, 200);
    			},
    			/*storeloaded: function(grid){
    				var f = Ext.getCmp('vo_currencytype');
    				if(f) {
    					me.changeCurrencyType(f);
    				}
    			},*/
    			itemclick: function(selModel, record){
    				var grid = selModel.ownerCt;
    				if(!grid.readOnly) {
    					this.GridUtil.onGridItemClick(selModel, record);
        				var btn = Ext.getCmp('replace');
        				btn.setDisabled(false);
        				btn = Ext.getCmp('level');
        				btn.setDisabled(false);
    				}
    			}
    		},
    		'voucherflowgrid': {
    			itemclick: function(selModel, record){
    				var grid = Ext.getCmp('flowgrid');
    				var index = record.data[grid.detno];
    				if(index == grid.store.data.items[grid.store.data.items.length-1].index + 1){
    		    		me.GridUtil.add10EmptyItems(grid);
    		    	}
    			}
    		},
    		'field[name=vo_currencytype]': {
    			change: function(c){
    				me.changeCurrencyType(c);
    			}
    		},
    		/**
    		 * 借调互换
    		 */
    		'button[id=replace]': {
    			click: function(btn){
    				var grid = btn.ownerCt.ownerCt;
    				var record = grid.selModel.lastSelected;
    				if(record){
    					var v1 = record.data['vd_debit'];//借方
    					var v2 = record.data['vd_credit'];//贷方
    					var v3 = record.data['vd_doubledebit'];//原币借方
    					var v4 = record.data['vd_doublecredit'];//原币贷方
    					record.set('vd_debit', v2);
    					record.set('vd_credit', v1);
    					record.set('vd_doubledebit', v4);
    					record.set('vd_doublecredit', v3);
    				}
    			}
    		},
    		/**
    		 * 找平
    		 */
    		'button[id=level]': {
    			click: me.levelOut
    		},
    		'field[name=vd_doubledebit]': {//原币借方
    			focus : function(f) {
    				var grid = Ext.getCmp('grid'),
    					record = grid.selModel.lastSelected,
    					val = record.get('vd_doublecredit');
    				if( val != 0 ) {
    					f.setReadOnly(true);
    				} else {
    					f.setReadOnly(false);
    				}
    			},
    			change: function(f){
    				if(!f.ownerCt && f.value != null && f.value != 0 ){
    					var grid = Ext.getCmp('grid');
    					var record = grid.selModel.lastSelected,
    						rate = record.data['vd_rate'];
    					if(rate != null && rate > 0){
    						var val = me.BaseUtil.numberFormat(me.BaseUtil.multiply(f.value, rate), 2);
    						if(record.data['vd_debit'] != val) {
    							record.set('vd_debit', val);//本币
    						}
    					}
    				}
    			}
    		},
    		'field[name=vd_doublecredit]': {//原币贷方
    			focus : function(f) {
    				var grid = Ext.getCmp('grid'),
    					record = grid.selModel.lastSelected,
    					val = record.get('vd_doubledebit');
    				if( val != 0 ) {
    					f.setReadOnly(true);
    				} else {
    					f.setReadOnly(false);
    				}
    			},
    			change: function(f){
    				if(!f.ownerCt && f.value != null && f.value != 0 ){
    					var record = Ext.getCmp('grid').selModel.lastSelected,
    						rate = record.data['vd_rate'];
    					if(rate != null && rate > 0){
    						var val = me.BaseUtil.numberFormat(me.BaseUtil.multiply(f.value, rate), 2);
    						if(record.data['vd_credit'] != val) {
    							record.set('vd_credit', val);//本币
    						}
    					}
    				}
    			}
    		},
    		'field[name=vd_currency]': {
    			afterrender: function(f){
    				f.mappingKey = 'cm_yearmonth';
    				f.dbKey = 'vo_yearmonth';
    			},
    			aftertrigger: function(f){
    				if(f.value != null && f.value != '' ){
    					var record = Ext.getCmp('grid').selModel.lastSelected;
    					if(record.data['vd_rate'] != null && record.data['vd_rate'] > 0){
    						if(record.data['vd_doubledebit'] != null){
            					record.set('vd_debit',
            							me.BaseUtil.numberFormat(me.BaseUtil.multiply(record.get('vd_doubledebit'), record.get('vd_rate')), 2));//原币计算本币
            				}
            				if(record.data['vd_doublecredit'] != null){
            					record.set('vd_credit', 
            							me.BaseUtil.numberFormat(me.BaseUtil.multiply(record.get('vd_doublecredit'), record.get('vd_rate')), 2));//原币计算本币
            				}
    					}
    				}
    			}
    		},
    		'field[name=vd_explanation]': {
    			specialkey: function(f, e){//按ENTER自动把摘要复制到下一行
    				if (e.getKey() == e.ENTER) {
    					if(f.value != null && f.value != '' ){
        					var grid = Ext.getCmp('grid'),
        						record = grid.selModel.lastSelected,
        						idx = grid.store.indexOf(record),
        						next = grid.store.getAt(idx + 1);
        					if(next) {
        						var v = next.get('vd_explanation');
        						if(Ext.isEmpty(v))
        							next.set('vd_explanation', f.value);
        					}
        				}
    				}
    			},
    			change: function(f) {
    				if(f.value == '=') {
    					var grid = Ext.getCmp('grid'),
							record = grid.selModel.lastSelected,
							idx = grid.store.indexOf(record),
							prev = grid.store.getAt(idx - 1);
    					if(prev) {
    						var v = prev.get('vd_explanation');
    						if(!Ext.isEmpty(v))
    							f.setValue(v);
    					}
    				}
    			}
    		},
    		'field[name=vd_debit]': {
    			focus : function(f) {
    				var grid = Ext.getCmp('grid'),
    					record = grid.selModel.lastSelected,
    					val = record.get('vd_credit');
    				if( val != 0 ) {
    					f.setReadOnly(true);
    				} else {
    					f.setReadOnly(false);
    				}
    			},
    			specialkey: function(f, e){//按ENTER自动把摘要复制到下一行
    				if (e.getKey() == e.ENTER) {
    					var grid = Ext.getCmp('grid'),
							record = grid.selModel.lastSelected,
							val = record.get('vd_explanation'),
							idx = grid.store.indexOf(record),
							next = grid.store.getAt(idx + 1);
    					if(!Ext.isEmpty(val)) {
    						if(next) {
    							var v = next.get('vd_explanation');
    							if(Ext.isEmpty(v))
    								next.set('vd_explanation', val);
    						}
    					}
    				}
    			}
    		},
    		'field[name=vd_credit]': {
    			focus : function(f) {
    				var grid = Ext.getCmp('grid'),
    					record = grid.selModel.lastSelected,
    					val = record.get('vd_debit');
    				if( val != 0 ) {
    					f.setReadOnly(true);
    				} else {
    					f.setReadOnly(false);
    				}
    			},
    			specialkey: function(f, e){//按ENTER自动把摘要复制到下一行
    				if (e.getKey() == e.ENTER) {
    					var grid = Ext.getCmp('grid'),
							record = grid.selModel.lastSelected,
							val = record.get('vd_explanation'),
							idx = grid.store.indexOf(record),
							next = grid.store.getAt(idx + 1);
    					if(!Ext.isEmpty(val)) {
    						if(next) {
    							var v = next.get('vd_explanation');
    							if(Ext.isEmpty(v))
    								next.set('vd_explanation', val);
    						}
    					}
    				}
    			}
    		},
    		'dbfindtrigger[name=vd_catecode]': {
    			aftertrigger: function(f){
    				var grid = Ext.getCmp('grid'),
    					record = grid.selModel.lastSelected;
    				var type = record.get('ca_assname'), ass = record.get('ass') || [];
    				if(!Ext.isEmpty(type)){
    					var oldType = Ext.Array.concate(ass, '#', 'vds_asstype');
    					if(type != oldType) {
    						var idx = me.getRecordIndex(grid, record), dd = [];
        					Ext.Array.each(type.split('#'), function(t){
        						dd.push({
        							vds_vdid: idx,
        							vds_asstype: t
        						});
        					});
        					record.set('ass', dd);
        					var view = grid.view, idx = grid.store.indexOf(record), rowNode = view.getNode(idx),
        						expander = grid.plugins[2], row = Ext.fly(rowNode, '_rowExpander'), 
        						isCollapsed = row.hasCls(expander.rowCollapsedCls);
        					if(isCollapsed)
        						expander.toggleRow(idx, record);
    					}
    				} else
    					record.set('ass', null);
    			}
    		},
    		'cateTreeDbfindTrigger[name=vd_catecode]': {
    			aftertrigger: function(f, d){
    				var grid = Ext.getCmp('grid'),
						record = grid.selModel.lastSelected;
					var type = record.get('ca_assname'), ass = record.get('ass') || [];
					if(!Ext.isEmpty(type)){
						var oldType = Ext.Array.concate(ass, '#', 'vds_asstype');
						if(type != oldType) {
							var idx = me.getRecordIndex(grid, record), dd = [];
	    					Ext.Array.each(type.split('#'), function(t){
	    						dd.push({
	    							vds_vdid: idx,
	    							vds_asstype: t
	    						});
	    					});
	    					record.set('ass', dd);
						}
					} else
						record.set('ass', null);
    			},
    			afterrender: function(f){
    				f.onTriggerClick = function(){
    					me.showCateTree(f);
    				};
    			}
    		},
    		/**
    		 * 导入凭证模板
    		 */
    		'erpExportTemplateButton': {
    			click: function() {
    				me.showTp();
    			}
    		},
    		/**
    		 * 添加到模板
    		 */
    		'erpCreateTemplateButton': {
    			click: function() {
    				me.createTp();
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	getRecordIndex: function(grid, record) {
		var me = this, id = record.get(grid.keyField);
		if(!id || id == 0) {
			me.rowCounter = me.rowCounter || 0;
			id = --me.rowCounter;
			record.set(grid.keyField, id);
		}
		return id;
	},
	changeCurrencyType: function(c){
		var grid = Ext.getCmp('grid');
		if(grid) {
			var cols = grid.headerCt.getGridColumns();
			if(c.checked){
				Ext.each(cols, function(cn){
					if(cn.dataIndex == 'vd_doubledebit' || cn.dataIndex == 'vd_doublecredit'){
						cn.setWidth(110);
						cn.setVisible(true);
					}
					if(cn.dataIndex == 'vd_currency' || cn.dataIndex == 'vd_rate'){
						cn.setWidth(60);
						cn.setVisible(true);
					}
					if(cn.dataIndex == 'vd_debit'){
						cn.setText('本币借方');
					}
					if(cn.dataIndex == 'vd_credit'){
						cn.setText('本币贷方');
					}
				});
			} else {
				Ext.each(cols, function(cn){
					if(cn.dataIndex == 'vd_currency' || cn.dataIndex == 'vd_rate'
						|| cn.dataIndex == 'vd_doubledebit' || cn.dataIndex == 'vd_doublecredit'){
						cn.setVisible(false);
					}
					if(cn.dataIndex == 'vd_debit'){
						cn.setText('借方');
					}
					if(cn.dataIndex == 'vd_credit'){
						cn.setText('贷方');
					}
				});
			}	
		}
	},
	beforeSave: function(){
		var me = this;
		var form = Ext.getCmp('form'), id = Ext.getCmp(form.keyField).value;
		var adjust = form.down('#vo_adjust'), status = form.down('#vo_statuscode'), ym = form.down('#vo_yearmonth');
		if (status.value == 'ENTERING') {
			if(adjust && adjust.value) {
				if(ym && !ym.value){
					showError('请先选择期间！');
					return;
				}
			}
		}
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.isEmpty(id) || id == 0 || id == '0'){
			me.FormUtil.getSeqId(form);
		}
		var detail = Ext.getCmp('grid');
		var flow = Ext.getCmp('flowgrid');
		var ass = [];
		detail.store.each(function(record){
			if(record.get('ca_assname')) {
				var s = record.get('ass') || [];
				Ext.Array.each(s, function(t, i){
					t.vds_id = t.vds_id || 0;
					t.vds_detno = i + 1;
					t.vds_vdid = String(t.vds_vdid);
					ass.push(t);
				});
			}
		});
		var param2 = new Array();
		if(flow) {
			param2 = me.GridUtil.getGridStore(flow);
		}
		var param3 = Ext.encode(ass);
		Ext.each(detail.store.data.items, function(item, idx){
			if(item.data.vd_id == null || item.data.vd_id == 0){
				item.data.vd_id = -idx;
			}
		});
		var param1 = me.GridUtil.getGridStore(detail);
		if(detail.necessaryField.length > 0 && (param1.length == 0)){
			showError($I18N.common.grid.emptyDetail);
			return;
		}
		var ex = new Array(),d;
		Ext.each(param1, function(){//摘要未填写
			d = Ext.decode(this);
			if(Ext.isEmpty(d.vd_explanation)) {
				ex.push(d.vd_detno);
			}
		});
		if(ex.length > 0) {
			warnMsg("摘要未填写，序号:" + ex.join(',') + " 是否继续保存?", function(btn){
				if(btn == 'yes') {
					me.onSave(form, param1, param2, param3);;
				}
			});
		} else {
			me.onSave(form, param1, param2, param3);
		}
	},
	onSave: function(form, param1, param2, param3) {
		var me = this;
		param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
		param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
		param3 = param3 == null ? [] : param3.toString().replace(/\\/g,"%");
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			r.vo_currencytype = Ext.getCmp('vo_currencytype').value ? -1 : 0;
			r.vo_errstring = r.vo_errstring == '正常' ? '' : r.vo_errstring;
			me.FormUtil.save(r, param1, param2, param3);
		}else{
			me.FormUtil.checkForm();
		}
	},
	beforeUpdate: function(){
		var me = this;
		var form = Ext.getCmp('form');
		var adjust = form.down('#vo_adjust'), status = form.down('#vo_statuscode'), ym = form.down('#vo_yearmonth');
		if (status.value == 'ENTERING') {
			if(adjust && adjust.value) {
				if(ym && !ym.value){
					showError('请先选择期间！');
					return;
				}
			}
		}
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var detail = Ext.getCmp('grid');
		var flow = Ext.getCmp('flowgrid');
		var param1 = me.GridUtil.getGridStore(detail);
		var param2 = new Array();
		if(flow) {
			param2 = me.GridUtil.getGridStore(flow);
		}
		var ass = [];
		detail.store.each(function(record){
			if(record.get('ca_assname')) {
				var s = record.get('ass') || [];
				Ext.Array.each(s, function(t, i){
					t.vds_id = t.vds_id || 0;
					t.vds_detno = i + 1;
					t.vds_vdid = String(t.vds_vdid);
					ass.push(t);
				});
			}
		});
		var param3 = Ext.encode(ass);
		if(me.FormUtil.checkFormDirty(form) == '' && detail.necessaryField.length > 0 && (param1.length == 0) && 
				(!flow || (flow.necessaryField.length > 0 && (param2.length == 0))) 
				&& (param3.length <= 2)){
			showError($I18N.common.grid.emptyDetail);
			return;
		}
		// 摘要未填写判断
		var ex = new Array(),d;
		Ext.each(param1, function(){
			d = Ext.decode(this);
			if(Ext.isEmpty(d.vd_explanation)) {
				ex.push(d.vd_detno);
			}
		});
		if(ex.length > 0) {
			warnMsg("摘要未填写，序号:" + ex.join(',') + " 是否继续保存?", function(btn){
				if(btn == 'yes') {
					me.onUpdate(form, param1, param2, param3);;
				}
			});
		} else {
			me.onUpdate(form, param1, param2, param3);
		}
	},
	onUpdate: function(form, param1, param2, param3) {
		var me = this;
		param1 = param1 == null ? [] : unescape("[" + param1.toString() + "]");
		param2 = param2 == null ? [] : unescape("[" + param2.toString() + "]");
		param3 = param3 == null ? [] : param3.toString();
		if(form.getForm().isValid()){
			Ext.each(form.items.items, function(item){
				if(item.xtype == 'numberfield'){
					if(item.value == null || item.value == ''){
						item.setValue(0);
					}
				}
			});
			var r = form.getValues();
			r.vo_currencytype = Ext.getCmp('vo_currencytype').value ? -1 : 0;
			r.vo_errstring = r.vo_errstring == '正常' ? '' : r.vo_errstring;
			me.FormUtil.update(r, param1, param2, param3);
		}else{
			me.FormUtil.checkForm();
		}
	},
	showVoucherFlow: function(){
		var win = Ext.getCmp('flow_win');
		var voucherid = Ext.getCmp('vo_id').value;
		if(!win) {
			Ext.create('Ext.Window', {
				id: 'flow_win',
				height: "100%",
				width: "80%",
				iconCls: 'x-button-icon-set',
				closeAction: 'hide',
				autoShow: true,
				title: '现金流',
				maximizable : true,
				layout : 'anchor',
				items: [{
					anchor: '100% 100%',
					xtype: 'voucherflowgrid',
					listeners: {
						afterrender: function(grid){
		    				if(formCondition == null || formCondition.toString().trim() == ''){
								grid.getMyData(-1);
							} else {
								grid.getMyData(voucherid);
							}
		    			}
					}
				}],
				buttonAlign: 'center',
				buttons: [{
					text: $I18N.common.button.erpConfirmButton,
					iconCls: 'x-button-icon-check',
			    	cls: 'x-btn-gray',
			    	handler: function(btn){
			    		btn.ownerCt.ownerCt.close();
			    	}
				}]
			});
		} else {
			win.show();
		}
	},
	/**
	 * 复制凭证
	 */
	copy: function(){
		var form = Ext.getCmp('form');
		var v = form.down('#vo_id').value;
		if(v > 0) {
			form.setLoading(true);
			Ext.Ajax.request({
				url: basePath + 'fa/ars/copyVoucher.action',
				params: {
					id: v
				},
				callback: function(opt, s, r){
					form.setLoading(false);
					var res = Ext.decode(r.responseText);
					if(res.voucher) {
						showMessage('提示', '复制成功！<a href="javascript:openUrl(\'jsps/fa/ars/voucher.jsp?formCondition=vo_idIS' + 
								 + res.voucher.vo_id + '&gridCondition=vd_voidIS' + res.voucher.vo_id + 
								'\')">\n凭证号:&lt;' + res.voucher.vo_number + 
								'&gt;\n流水号:&lt;' + res.voucher.vo_code + '&gt;</a>');
					} else {
						showError(res.exceptionInfo);
					}
				}
			});
		}
	},
	//凭证红冲
	rushRed: function(){
		var form = Ext.getCmp('form');
		var v = form.down('#vo_id').value;
		if(v > 0) {
			form.setLoading(true);
			Ext.Ajax.request({
				url: basePath + 'fa/ars/rushRedVoucher.action',
				params: {
					id: v
				},
				callback: function(opt, s, r){
					form.setLoading(false);
					var res = Ext.decode(r.responseText);
					if(res.voucher) {
						showMessage('提示', '红冲凭证产生成功!<a href="javascript:openUrl(\'jsps/fa/ars/voucher.jsp?formCondition=vo_idIS' + 
								 + res.voucher.vo_id + '&gridCondition=vd_voidIS' + res.voucher.vo_id + 
								'\')">\n凭证号:&lt;' + res.voucher.vo_number + 
								'&gt;\n流水号:&lt;' + res.voucher.vo_code + '&gt;</a>');
					} else {
						showError(res.exceptionInfo);
					}
				}
			});
		}
	},
	getSource: function(id) {
		var me = this;
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsData.action',
	   		params: {
	   			caller: 'VoucherBill',
	   			fields: 'vb_billcode,vb_vscode',
	   			condition: 'vb_void=' + id
	   		},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);
	   			} else if(localJson.success){
    				if(localJson.data != null && localJson.data.vb_vscode != null){
    					me.showSourceList(localJson.data.vb_vscode, localJson.data.vb_billcode);
    				} else {
    					showMessage('提示', '当前凭证没有来源信息!');
    				}
	   			}
	   		}
		});
	},
	showSourceList: function(vscode, pri) {
		var me = this, cls = Ext.getCmp('vo_source').value,
			cfg = me.getSourceConfig(vscode, cls), 
			merge = pri.indexOf(',') > 0 || /SELECT.*FROM.*/.test(pri.toUpperCase());
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsData.action',
	   		params: {
	   			caller: 'VoucherStyle',
	   			fields: 'vs_datalist,vs_prikey1,vs_pritable,vs_classfield',
	   			condition: 'vs_code=\'' + vscode + '\''
	   		},
	   		method : 'post',
	   		callback : function(opt, s, res){
	   			var r = new Ext.decode(res.responseText);
	   			if (r.exceptionInfo) {
	   				showError(r.exceptionInfo);
	   			} else if(r.success) {
    				if (r.data != null) {
    					if (merge || !cfg.url) {
    						if(r.data.vs_datalist)
    							me.showSourceGrid(vscode, r.data.vs_datalist, r.data.vs_prikey1, 
    								r.data.vs_pritable, r.data.vs_classfield, cls, pri, cfg);
    						else
    							showError('凭证公式未配置列表参数，无法显示来源！');
    					} else
    						me.linkSource(vscode, r.data.vs_prikey1, r.data.vs_pritable,
    								r.data.vs_classfield, cls, pri, cfg);
    				} else {
    					showMessage('提示', '当前凭证没有来源信息!', 3000);
    				}
	   			}
	   		}
		});
	},
	linkSource : function(vscode, key, tab, clsfield, cls, pri, cfg) {
		var me = this, url = cfg.url, k = cfg.keyfield, m = cfg.mainfield;
		if (url) {
			Ext.Ajax.request({
		   		url : basePath + 'common/getFieldData.action',
		   		async: false,
		   		params: {
		   			caller: tab,
		   			field: k,
		   			condition: key + '=' + pri + (clsfield ? (' and ' + clsfield + '=\'' + cls + '\'') : '')
		   		},
		   		method : 'post',
		   		callback : function(opt, s, res){
		   			var r = new Ext.decode(res.responseText);
		   			if(r.exceptionInfo){
		   				showError(r.exceptionInfo);
		   			} else if(r.success && r.data){
		   				url += url.indexOf('?') > 0 ? '&' : '?';
		   				me.FormUtil.onAdd(null, '来源', url + 'formCondition=' + k + 'IS' +
    							r.data + '&gridCondition=' + m + 'IS' + r.data);
		   			}
		   		}
			});
		}
	},
	showSourceGrid : function(vscode, cal, key, tab, clsfield, cls, pri, cfg) {
		var me = this,
			condition = key + ' in (' + pri + ')';
		if('PRODINOUT' == vscode) {
			condition += ' and pi_class=\'' + cls + '\'';
		}
		var w = Ext.isIE ? 800 : '80%',
			h = Ext.isIE ? 500 : '100%'; 
		var win = Ext.create('Ext.Window', {
			width: w,
			height: h,
			title: '凭证来源',
			layout: 'anchor',
			items: [],
			buttonAlign: 'center',
			buttons: [{
				text: $I18N.common.button.erpExportButton,
				cls: 'x-btn-gray',
				iconCls: 'x-button-icon-ecel',
				handler: function(btn) {
					me.BaseUtil.exportGrid(btn.ownerCt.ownerCt.down('grid'));
					btn.ownerCt.ownerCt.close();
				}
			},{
				text: $I18N.common.button.erpCloseButton,
				cls: 'x-btn-gray',
				iconCls: 'x-button-icon-close',
				handler: function(btn) {
					btn.ownerCt.ownerCt.close();
				}
			}]
		}).show();
		var grid = Ext.create('Ext.grid.Panel', {
			anchor: '100% 100%',
	    	columnLines: true,
	    	columns: [],
	    	store: []
	    });
	    win.add(grid);
		me.GridUtil.getGridColumnsAndStore(grid, 'common/singleGridPanel.action', {caller: cal, condition: condition});
		grid.on('itemclick', function(s, r){
			me.linkSource(vscode, key, tab, clsfield, cls, "\'" + r.get(key) + "\'", cfg);
		});
	},
	getSourceConfig : function(vscode, vdclass) {
		var url = null, keyfield = null, mainfield = null;
		if(vscode == 'RecBalance') {
			keyfield = 'rb_id';
			mainfield = 'rbd_rbid';
			switch (vdclass) {
				case '应收冲应付':
					url = 'jsps/fa/ars/recBalanceAP.jsp?whoami=RecBalance!RRCW';
					break;
				case '收款单':
					url = 'jsps/fa/ars/recBalance.jsp?whoami=RecBalance!PBIL';
					break;
				case '冲应收款':
					url = 'jsps/fa/ars/recBalance.jsp?whoami=RecBalance!IMRE';
					break;
				case '预收冲应收':
					url = 'jsps/fa/ars/recBalancePRDetail.jsp?whoami=RecBalance!PTAR';
					break;
				case '应收票据':
					url = 'jsps/fa/gs/billAR.jsp';
					break;
				case '应收款转销':
					url = 'jsps/fa/ars/recBalance.jsp?whoami=RecBalance!ARRM';
					break;
				case '应收退款单':
					url = 'jsps/fa/ars/recBalanceTK.jsp?whoami=RecBalance!TK';
					break;
			}
		} else if(vscode == 'PreRec') {
			keyfield = 'pr_id';
			mainfield = 'prd_prid';
			switch (vdclass) {
				case '预收款':
					url = 'jsps/fa/ars/preRec.jsp?whoami=PreRec!Ars!DERE';
					break;
				case '预收退款':
					url = 'jsps/fa/ars/preRec.jsp?whoami=PreRec!Ars!DEPR';
					break;
				case '预收退款单':
					url = 'jsps/fa/ars/preRec.jsp?whoami=PreRec!Ars!DEPR';
					break;
			}
		} else if(vscode == 'PrePay') {
			keyfield = 'pp_id';
			mainfield = 'ppd_ppid';
			switch (vdclass) {
				case '预付款':
					url = 'jsps/fa/arp/prepay.jsp?whoami=PrePay!Arp!PAMT';
					break;
				case '预付退款':
					url = 'jsps/fa/arp/prepay.jsp?whoami=PrePay!Arp!PAPR';
					break;
				case '预付退款单':
					url = 'jsps/fa/arp/prepay.jsp?whoami=PrePay!Arp!PAPR';
					break;
			}
		} else if(vscode == 'PrePaid') {
			keyfield = 'pp_id';
			mainfield = 'pd_ppid';
			switch (vdclass) {
				case '摊销':
					url = 'jsps/fa/gla/prePaid.jsp';
					break;
			}
		} else if(vscode == 'PurcFee') {
			keyfield = 'pi_id';
			mainfield = 'pd_piid';
			switch (vdclass) {
				case '采购验收单':
					url = 'jsps/scm/reserve/prodinout.jsp?whoami=ProdInOut!PurcCheckin';
					break;
				case '采购验退单':
					url = 'jsps/scm/reserve/prodinout.jsp?whoami=ProdInOut!PurcCheckout';
					break;
			}
		} else if(vscode == 'PayBalance') {
			keyfield = 'pb_id';
			mainfield = 'pbd_pbid';
			switch (vdclass) {
				case '冲应付款':
					url = 'jsps/fa/arp/paybalance.jsp?whoami=PayBalance!CAID';
					break;
				case '应付款转销':
					url = 'jsps/fa/arp/paybalance.jsp?whoami=PayBalance!APRM';
					break;
				case '预付冲应付':
					url = 'jsps/fa/arp/payBalancePRDetail.jsp?whoami=PayBalance!Arp!PADW';
					break;
				case '付款单':
					url = 'jsps/fa/arp/paybalance.jsp?whoami=PayBalance';
					break;
				case '应付冲应收':
					url = 'jsps/fa/arp/paybalance.jsp?whoami=PayBalance!DWRC';
					break;
				case '应付退款单':
					url = 'jsps/fa/arp/paybalanceTK.jsp?whoami=PayBalance!TK';
					break;
			}
		} else if(vscode == 'AccountRegiste') {
			keyfield = 'ar_id';
			mainfield = 'ard_arid';
			url = 'jsps/fa/gs/accountRegister.jsp?whoami=AccountRegister!Bank';
		} else if(vscode == 'Estimate') {
			keyfield = 'es_id';
			mainfield = 'esd_esid';
			url = 'jsps/fa/arp/estimate.jsp?whoami=Estimate';
		} else if(vscode == 'GoodsSend') {
			keyfield = 'gs_id';
			mainfield = 'gsd_gsid';
			url = 'jsps/fa/ars/goodsSend.jsp?whoami=GoodsSendGs';
		} else if(vscode == 'ARBill') {
			keyfield = 'ab_id';
			mainfield = 'abd_abid';
			switch (vdclass) {
				case '其它应收单':
					url = 'jsps/fa/ars/arbill.jsp?whoami=ARBill!OTRS';
					break;
				case '应收发票':
					url = 'jsps/fa/ars/arbill.jsp?whoami=ARBill!IRMA';
					break;
			}
		} else if(vscode == 'APBill') {
			keyfield = 'ab_id';
			mainfield = 'abd_abid';
			switch (vdclass) {
				case '其它应付单':
					url = 'jsps/fa/ars/apbill.jsp?whoami=APBill!OTDW';
					break;
				case '应付发票':
					url = 'jsps/fa/ars/apbill.jsp?whoami=APBill!CWIM';
					break;
			}
		} else if(vscode == 'BillOut') {
			keyfield = 'bi_id';
			mainfield = 'ard_biid';
			url = 'jsps/fa/ars/billOut.jsp';
		} else if(vscode == 'BillOutAP') {
			keyfield = 'bi_id';
			mainfield = 'ard_biid';
			url = 'jsps/fa/arp/billOutAP.jsp';
		} else if(vscode == 'AssetsCard') {
			keyfield = 'ac_id';
			url = 'jsps/fa/fix/assetsCard.jsp';
		} else if(vscode == 'Depreciation') {
			keyfield = 'de_id';
			mainfield = 'dd_deid';
			switch (vdclass) {
				case '折旧单':
					url = 'jsps/fa/fix/assetsDepreciation.jsp?whoami=AssetsDepreciation';
					break;
				case '资产增加单':
					url = 'jsps/fa/fix/assetsDepreciation.jsp?whoami=AssetsDepreciation!Add';
					break;
				case '资产减少单':
					url = 'jsps/fa/fix/assetsDepreciation.jsp?whoami=AssetsDepreciation!Reduce';
					break;
			}
		} else if(vscode == 'PRODINOUT') {
			keyfield = 'pi_id';
			mainfield = 'pd_piid';
			url = 'jsps/scm/reserve/prodInOut.jsp';
			var call;
			switch (vdclass) {
			case '采购验收单':
	    		call = 'ProdInOut!PurcCheckin';
	    		break;
	    	case '采购验退单':
	    		call = 'ProdInOut!PurcCheckout';
	    		break;
	    	case '出货单':
	    		call = 'ProdInOut!Sale';
	    		break;
	    	case '拨入单':
	    		call = 'ProdInOut!AppropriationIn';
	    		break;
	    	case '销售拨出单':
	    		call = 'ProdInOut!SaleAppropriationOut';
	    		break;
	    	case '销售退货单':
	    		call = 'ProdInOut!SaleReturn';
	    		break;
	    	case '拨出单':
	    		call = 'ProdInOut!AppropriationOut';
	    		break;
	    	case '不良品入库单':
	    		call = 'ProdInOut!DefectIn';
	    		break;
	    	case '不良品出库单':
	    		call = 'ProdInOut!DefectOut';
	    		break;
	    	case '委外领料单':
	    		call = 'ProdInOut!OutsidePicking';
	    		break;
	    	case '委外退料单':
	    		call = 'ProdInOut!OutsideReturn';
	    		break;
	    	case '委外验收单':
	    		call = 'ProdInOut!OutsideCheckIn';
	    		break;
	    	case '委外验退单':
	    		call = 'ProdInOut!OutesideCheckReturn';
	    		break;
	    	case '借货归还单':
	    		call = 'ProdInOut!OutReturn';
	    		break;
	    	case '研发采购验收单':
	    		call = 'ProdInOut!PurcCheckin!PLM';
	    		break;
	    	case '研发采购验退单':
	    		call = 'ProdInOut!PurcCheckout!PLM';
	    		break;
	    	case '换货入库单':
	    		call = 'ProdInOut!ExchangeIn';
	    		break;
	    	case '换货出库单':
	    		call = 'ProdInOut!ExchangeOut';
	    		break;
	    	case '生产补料单':
	    		call = 'ProdInOut!Make!Give';
	    		break;
	    	case '完工入库单':
	    		call = 'ProdInOut!Make!In';
	    		break;
	    	case '生产退料单':
	    		call = 'ProdInOut!Make!Return';
	    		break;
	    	case '生产报废单':
	    		call = 'ProdInOut!Make!Useless';
	    		break;
	    	case '无订单出货单':
	    		call = 'ProdInOut!NoSale';
	    		break;
	    	case '委外补料单':
	    		call = 'ProdInOut!OSMake!Give';
	    		break;
	    	case '其它入库单':
	    		call = 'ProdInOut!OtherIn';
	    		break;
	    	case '其它出库单':
	    		call = 'ProdInOut!OtherOut';
	    		break;
	    	case '其它采购入库单':
	    		call = 'ProdInOut!OtherPurcIn';
	    		break;
	    	case '其它采购出库单':
	    		call = 'ProdInOut!OtherPurcOut';
	    		break;
	    	case '拆件入库单':
	    		call = 'ProdInOut!PartitionStockIn';
	    		break;
	    	case '生产领料单':
	    		call = 'ProdInOut!Picking';
	    		break;
	    	case '库存初始化':
	    		call = 'ProdInOut!ReserveInitialize';
	    		break;
	    	case '借货出货单':
	    		call = 'ProdInOut!SaleBorrow';
	    		break;
	    	case '销售拨入单':
	    		call = 'ProdInOut!SaleAppropriationIn';
	    		break;
	    	case '盘亏调整单':
	    		call = 'ProdInOut!StockLoss';
	    		break;
	    	case '盘盈调整单':
	    		call = 'ProdInOut!StockProfit';
	    		break;
	    	case '报废单':
	    		call = 'ProdInOut!StockScrap';
	    		break;
	    	case '研发退料单':
	    		call = 'ProdInOut!YFIN';
	    		break;
	    	case '研发领料单':
	    		call = 'ProdInOut!YFOUT';
	    		break;
	    	case '成本调整单':
	    		call = 'ProdInOut!CostChange';
	    		break;
	    	case '用品领用单':
	    		call = 'ProdInOut!GoodsPicking';
	    		break;
	    	case '用品退仓单':
	    		call = 'ProdInOut!GoodsShutout';
	    		break;
	    	case '借货出货单':
	    		call = 'ProdInOut!SaleBorrow';
	    		break;
	    	case '借货归还单':
	    		call = 'ProdInOut!OutReturn';
	    		break;
	    	case '辅料入库单':
	    		call = 'ProdInOut!FLIN';
	    		break;
	    	case '辅料出库单':
	    		call = 'ProdInOut!FLOUT';
	    		break;
	    	case '配货单':
	    		call = 'ProdInOut!DrpSale';
	    		break;
	    	}
			call && (url += '?whoami=' + call);
		} else if(vscode == 'BillAR') {
			url = 'jsps/fa/gs/billAR.jsp';
			keyfield = 'bar_id';
		} else if(vscode == 'BillAP') {
			url = 'jsps/fa/gs/billAP.jsp';
			keyfield = 'bap_id';
		} else if(vscode == 'BillARChange') {
			url = 'jsps/fa/gs/billARChange.jsp';
			keyfield = 'brc_id';
			mainfield = 'brd_brcid';
		} else if(vscode == 'BillAPChange') {
			url = 'jsps/fa/gs/billAPChange.jsp';
			keyfield = 'bpc_id';
			mainfield = 'bpd_bpcid';
		}
		return {url : url, keyfield : keyfield, mainfield : mainfield};
	},
	levelOut:function (target){
		var grid = Ext.getCmp('grid'), me = this;
		var record = grid.selModel.lastSelected;
		if(record){
			var f = Ext.getCmp('vo_currencytype');
			var debit = 0;
			var credit = 0;
			var rate = record.get('vd_rate');
			rate = rate == 0 ? 1 : rate;
			grid.getStore().each(function(item){
				if(item.id != record.id){
					debit += item.get('vd_debit');
					credit += item.get('vd_credit');
				}
			});
			var targetName = target.name;
			if(record.get('vd_debit') != 0)
				targetName = 'vd_debit';
			else if(record.get('vd_credit') != 0)
				targetName = 'vd_credit';
			if(targetName && typeof targetName == 'string') {
				if(targetName == 'vd_debit') {
					debit = credit - debit;
					record.set('vd_debit', me.BaseUtil.numberFormat(debit, 4));
					if(f.checked) {
						record.set('vd_doubledebit', me.BaseUtil.numberFormat(debit/rate, 4));
					}
				} else if(targetName == 'vd_credit'){
					credit = debit - credit;
					record.set('vd_credit', me.BaseUtil.numberFormat(credit, 4));
					if(f.checked) {
						record.set('vd_doublecredit', me.BaseUtil.numberFormat(credit/rate, 4));
					}
				}
				if(target.name == targetName)
					target.value = record.get(targetName);
			} else {
				if(credit > debit) {
					record.set('vd_debit', credit - debit);
					if(f.checked) {
						record.set('vd_doubledebit', me.BaseUtil.numberFormat((credit - debit)/rate, 4));
					}
				} else {
					record.set('vd_credit', debit - credit);
					if(f.checked) {
						record.set('vd_doublecredit', me.BaseUtil.numberFormat((debit - credit)/rate, 4));
					}
				}
			}
		}
	},
	showCateTree: function(f) {
		var cawin = Ext.getCmp('cawin');
		if(!cawin) {
			cawin = new Ext.window.Window({
	    		id : 'cawin',
			    title: '科目查找',
			    height: "100%",
			    width: "80%",
			    maximizable : true,
				buttonAlign : 'center',
				layout : 'anchor',
				modal:true,
			    items: [{
			    	tag : 'iframe',
			    	frame : true,
			    	anchor : '100% 100%',
			    	layout : 'fit',
			    	html : '<iframe id="iframe_dbfind_'+caller+"_"+f.name+"="+f.value+'" src="'+basePath+'jsps/common/catetreepaneldbfind.jsp?key='+f.name+"&dbfind=&caller1="+caller+"&keyValue="+f.value+"&trigger="+f.id+'" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
			    }],
			    buttons : [{
			    	text : '确  认',
			    	iconCls: 'x-button-icon-save',
			    	cls: 'x-btn-gray',
			    	handler : function(btn){
			    		var contentwindow = Ext.getCmp('cawin').body.dom.getElementsByTagName('iframe')[0].contentWindow;
			    		var tree = contentwindow.Ext.getCmp('tree-panel');
			    		var data = tree.getChecked(), record = Ext.getCmp('grid').selModel.lastSelected;
			    		var dbfinds = Ext.getCmp('grid').dbfinds;
			    		if(dbfinds != null && record){
			    			Ext.each(dbfinds, function(dbfind,index){
			    				record.set(dbfind.field, data[0].raw.data[dbfind.dbGridField]);
				    		});
			    		}
			    		f.fireEvent('aftertrigger', f, data);
			    		btn.ownerCt.ownerCt.hide();
			    	}
			    },{
			    	text : '关  闭',
			    	iconCls: 'x-button-icon-close',
			    	cls: 'x-btn-gray',
			    	handler : function(btn){
			    		btn.ownerCt.ownerCt.hide();
			    	}
			    }]
			});
		}
		cawin.show();
	},
    getMonth: function(callback) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/getMonth.action',
    		params: {
    			votype: 'GL'
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				callback.call(null, rs.data);
    			}
    		}
    	});
    },
    /**
     * 显示模板
     */
    showTp: function() {
    	var me = this, win = me.tpWin;
    	if(!win) {
    		win = me.tpWin = new Ext.window.Window({
    			id: 'tp-win',
    			width: '84%',
    			height: '100%',
    			title: '选择模板',
    			layout: 'anchor',
    			closeAction: 'hide',
    			items: [{
    				xtype: 'container',
    				anchor: '100% 100%',
                    html: '<iframe src="' + basePath + 'jsps/common/datalist.jsp?whoami=Voucher!TP&&parentDoc=tp-win" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
    			}],
    			listeners: {
    				'itemselect': function(scope, data) {
    					me.loadTp(data.vo_id);
    					scope.hide();
    				},
    				'close': function(scope) {
    					scope.hide();
    				}
    			}
    		});
    	}
    	win.show();
    },
    /**
     * 加载模板
     */
    loadTp: function(id) {
    	Ext.Ajax.request({
    		url: basePath + 'fa/gla/getvotp.action',
    		params: {
    			id: id
    		},
    		callback: function(opt, s, r) {
    			var rs = Ext.decode(r.responseText);
    			if(rs.data) {
    				var form = Ext.getCmp('form'), grid = Ext.getCmp('grid');
    				form.getForm().setValues(rs.data.voucher);
    				grid.store.loadData(rs.data.voucherdetail);
    				var store = grid.store, i = 0, exp = grid.plugins[2];
    				grid.store.each(function(record){
    					exp.toggleRow(i++);
    					record.dirty = true;
    				});
    			} else {
    				showError(rs.exceptionInfo);
    			}
    		}
    	});
    },
	/**
	 * 添加到凭证模板
	 */
	createTp: function(){
		var form = Ext.getCmp('form');
		var v = form.down('#vo_id').value;
		if(v > 0) {
			form.setLoading(true);
			Ext.Ajax.request({
				url: basePath + 'fa/gla/copyvotp.action',
				params: {
					id: v
				},
				callback: function(opt, s, r){
					form.setLoading(false);
					var res = Ext.decode(r.responseText);
					if(res.data) {
						showMessage('提示', '添加成功！<a href="javascript:openUrl(\'jsps/fa/gla/vouchertp.jsp?formCondition=vo_idIS' + 
								 + res.data.vo_id + '&gridCondition=vd_voidIS' + res.data.vo_id + 
								'\')"> \n流水号:&lt;' + res.data.vo_code + '&gt;</a>');
					} else {
						showError(res.exceptionInfo);
					}
				}
			});
		}
	},
	loadAdjustData: function(form) {
		var me = this, ym = form.down('#vo_yearmonth'), adjust = form.down('#vo_adjust'),
			status = form.down('#vo_statuscode'), date = form.down('#vo_date');
		if (status.value == 'ENTERING') {
			if(adjust && adjust.value != 0) {
				date.setReadOnly(true);
				me.getComboData(function(data){
					var val = ym.auditValue || ym.value;
					if (!ym.is('combo')) {
						var index = 2, col = ym.columnWidth, label = ym.fieldLabel, cls = ym.cls,
							fcls = ym.fieldCls, style = ym.fieldStyle;
						index = (form.getForm().getFields().indexOf(ym) || index) - 1;
						form.remove(ym);
						ym = form.insert(index, {
							xtype: 'combo',
							id: 'vo_yearmonth',
							name: 'vo_yearmonth',
							cls: cls,
							fieldCls: fcls,
							fieldLabel: label,
							fieldStyle: style,
							columnWidth: col,
							labelAlign: 'left',
							queryMode: 'local',
							displayField: 'display',
							valueField: 'value',
							editable: false,
							store: new Ext.data.Store({
								fields: ['display', 'value', 'ad_date']
							}),
							value: val
						});
					}
					if(data.length == 0){
						showError('请先设置审计期间！');
						return;
					}
					ym.store.loadData(data);
					ym.setReadOnly(false);
					ym.setValue(val);
				});
			} else if(ym){
				ym.setReadOnly(true);
				date.setReadOnly(false);
				if (ym.is('combo')) {
					var index = 1, col = ym.columnWidth, label = ym.fieldLabel, 
						fcls = ym.fieldCls, style = ym.fieldStyle, val = ym.value;
					index = (form.getForm().getFields().indexOf(ym) || index) - 1;
					form.remove(ym);
					ym = form.insert(index, {
						xtype: 'numberfield',
						id: 'vo_yearmonth',
						name: 'vo_yearmonth',
						cls: 'u-form-default',
						fieldCls: fcls,
						fieldLabel: label,
						fieldStyle: style,
						readOnly: true,
						hideTrigger: true,
						columnWidth: col,
						labelAlign: 'left'
					});
					ym.auditValue = val;
				}
				me.setVoucherMonth(form);
			}
		}
	},
	getComboData: function(callback) {
 		var me = this;
 		Ext.Ajax.request({
    		url : basePath + 'common/getFieldsDatas.action',
       		async: false,
       		params: {
       			caller: 'AuditDuring',
       			fields: 'ad_yearmonth, ad_remark, ad_date',
 				condition: 'ad_isuse=1'
       		},
       		method : 'post',
       		callback : function(options,success,response){
       			var rs = new Ext.decode(response.responseText);
       			if(rs.exceptionInfo){
       				showError(rs.exceptionInfo);return null;
       			}
    			if(rs.success && rs.data){
    				var data = Ext.decode(rs.data), arr = new Array();
 	  				for(var i in data) {
 	  					arr.push({
 	  						display: data[i].AD_YEARMONTH,
 	  						value: data[i].AD_YEARMONTH,
 	  						ad_date: data[i].AD_DATE
 	  					});
 	  				}
    				callback.call(me, arr);
    			}
       		}
    	});
 	},
 	setVoucherMonth: function(form) {
 		var me = this;
 		Ext.defer(function(){
			var id = form.down('#vo_id').getValue(), adjust = form.down('#vo_adjust');
			me.getMonth(function(data){
				if (Ext.isEmpty(id) || id == 0) {
					Ext.getCmp('vo_yearmonth').setValue(data.PD_DETNO);
					Ext.getCmp('vo_date').setValue(new Date(data.PD_ENDDATE));
					if(String(data.PD_DETNO).length > 6 && adjust){
						adjust.setValue(true);
					}
				} else {
					var ym = Ext.getCmp('vo_yearmonth').getValue() || Ext.Date.format(new Date(), 'Ym'),
					    pd_detno = String(data.PD_DETNO).substring(0,6);
					if(ym < pd_detno) {
						form.readOnly = true;
						Ext.getCmp('grid').readOnly = true;
					}
				}
			});
		}, 20);
 	}
});