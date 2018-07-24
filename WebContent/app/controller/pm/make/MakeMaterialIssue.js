Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakeMaterialIssue', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.RenderUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
	views: ['pm.make.MakeMaterialIssue', 'core.grid.Panel5', 'common.editorColumn.GridPanel', 'core.grid.YnColumn', 'core.button.CreateDetail', 'core.button.PrintDetail', 'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger'],
	init: function() {
		var me = this;
		me.GridUtil = Ext.create('erp.util.GridUtil');
		me.BaseUtil = Ext.create('erp.util.BaseUtil'); 
		this.control({
			'button[id=create]': {
				click: function(btn) {
					var grid = Ext.getCmp('editorColumnGridPanel');
					var e = me.checkQty(Ext.getCmp('grid'), grid);
					if (e.length > 0) {
						showError(e);
						return;
					}
					e = me.check(grid.selModel.getSelection());
					if (e.length > 0) {
						showError(e);
						return;
					}
					warnMsg("确定要生成领料单吗?",
							function(btn) {
						if (btn == 'yes') {
							me.turnOut(grid);
						}
					});
				}
			},
			'button[id=createbc]': {
				click: function(btn) {
					var grid = Ext.getCmp('editorColumnGridPanel');
					var e = me.checkQty(Ext.getCmp('grid'), grid);
					if (e.length > 0) {
						showError(e);
						return;
					}
					e = me.check(grid.selModel.getSelection());
					if (e.length > 0) {
						showError(e);
						return;
					}
					warnMsg("确定要生成拨出单吗?",
							function(btn) {
						if (btn == 'yes') {
							me.turnProdIOBC(grid);
						}
					});
				},
				beforerender:function(btn){
					me.BaseUtil.getSetting('MakeMaterial!issue', 'ifShowTurnOutButton', function(v) {
						if(v){
							btn.show();							
						}
					});
				}
			},
			'button[name=query]': {
				click: function(btn) {
					me.onQuery();
				}
			},
			'checkbox[id=whcode]': {
				afterrender: function(f) {
					me.BaseUtil.getSetting('MakeMaterial!issue', 'groupWarehouse', function(v) {					
						f.setValue(v);
					});										
				}
			},
			'checkbox[id=set]': {
				afterrender: function(f) {
					me.BaseUtil.getSetting('MakeMaterial!issue', 'setOfMaterial', function(v) {
						f.setValue(v);
					});
					me.BaseUtil.getSetting('MakeMaterial!issue', 'ifCanrepqty', function(v) {
						ifCanrepqty = v;					
					});
					me.BaseUtil.getSetting('MakeMaterial!issue', 'includingLoss', function(v) {//套料数发料是否包含损耗
						ifIncludingLoss = v;					
					});
					me.BaseUtil.getSetting('MakeMaterial!issue', 'showUserFactoryWh', function(v) {//物料分仓库存只显示登录用户所属工厂对应仓库库存信息
						var grid = Ext.getCmp('editorColumnGridPanel');
						grid.ifOnlyShowUserFactoryWh = v||false;					
					});
				}
			},
			'checkbox[id=ifnullwhman]': {
				afterrender: function(f) {
					me.BaseUtil.getSetting('MakeMaterial!issue', 'ifnullwhman', function(v) {
						f.setValue(v);
					});
				}
			},
			'checkbox[id=ifnulllocation]': {
				afterrender: function(f) {
					me.BaseUtil.getSetting('MakeMaterial!issue', 'ifnulllocation', function(v) {
						f.setValue(v);
					});
				}
			},
			'dbfindtrigger[id=st_code]':{
				beforerender: function(f) {
					me.BaseUtil.getSetting('MakeMaterial!issue', 'supportStepFilter', function(v) {
						if(v){
							f.show();							
						}
					});
				}
			},
			'dbfindtrigger[name = mm_whcode]':{
				aftertrigger:function(f){
					var newvalue = f.value;
					var mm_id = f.record.data.mm_id;
					var isrep = f.record.data.isrep;
					var mpdetno = f.record.data.mm_detno;
					me.BaseUtil.getSetting('MakeMaterial!issue', 'changeWhCode', function(v) {
						if(v){
							Ext.Ajax.request({
								url: basePath + 'pm/make/changeWhcode.action',
								params: {
									isrep: isrep,
									whcode: newvalue,
									mmid: mm_id,
									mpdetno:mpdetno
								},
								method: 'post',
								callback: function(options, success, response) {
									var localJson = new Ext.decode(response.responseText);
									if (localJson.exceptionInfo) {
										showError(localJson.exceptionInfo);
									}	   													
								}
							});
						}
					});
				
				}
			},
			'dbfindtrigger[name = ma_code]':{
				beforetrigger: function(f) {
					var wccode = getUrlParam("wccode");
    				if(wccode && wccode!='' && wccode!=null){
						f.dbBaseCondition = wccode;
    				}
				}
			},
			'textfield[id=pr_location]': {
				specialkey: function(field, e) {
					if (e.getKey() == Ext.EventObject.ENTER) {
						me.onQuery();
					}
				}
			},
			'textfield[id=ma_wccode]': {
				specialkey: function(field, e) {
					if (e.getKey() == Ext.EventObject.ENTER) {
						me.onQuery();
					}
				}
			},
			'combo[id=groupPurs]': {
				beforerender: function(f) {
					me.BaseUtil.getSetting('MakeMaterial!issue', 'isGroupPurc', function(v) {
						if(v){
							f.show();							
						}
					});
				}
			},
			'erpEditorColumnGridPanel':{
				storeloaded:function(grid){
					//zhouy  没有找到更好的解决 锁定列与normalview对不齐的方式  暂时这样处理
					Ext.defer(function(){
						var lockedView = grid.view.lockedView;
						if(lockedView){
							var tableEl = lockedView.el.child('.x-grid-table');
							if(tableEl){
								tableEl.dom.style.marginBottom = '7px';
							}
						}
						//lockedView.doLayout();
					}, 100);
				},
				afterrender : function(f) {
					me.BaseUtil.getSetting('MakeMaterial!issue', 'Select!OS!issue', function(bool) {
						isSelect = bool;
                    });
				},
				selectionchange:function(selectionModel, selected, options){
					if(!isSelect){
						Ext.each(selected,function(item){
							var sum=0;
							var ma_code = item.data['mm_code'];
							var mm_detno = item.data['mm_id'];
							Ext.each(selected,function(s){
								if(ma_code==s.data['mm_code'] && mm_detno==s.data['mm_id']){
									sum+=s.data['mm_thisqty'];
								}
							});
							Ext.each(selected,function(a){
								if(ma_code==a.data['mm_code'] && mm_detno==a.data['mm_id']){
									a.set('mm_total', sum);
								}
							});
						});
					}
				},
				deselect:function(row,record,index,eOpts){
					record.set('mm_total',0);
				},
				edit:function(e,o,eOpts){
//					if(o.originalValue<o.value){
//						return;
//					}
					var grid = Ext.getCmp('editorColumnGridPanel');
					grid.fireEvent('selectionchange',grid.selModel.selectionMode, grid.selModel.selected.items, '');
				}
			},
			'checkbox[id=showZeroQty]': {  //显示需求数为0的物料
				beforerender: function(f) {
					me.BaseUtil.getSetting('MakeMaterial!issue', 'showZeroQty', function(v) {
						if(v){
							f.show();							
						}
					});
				},
				change: function(field,n,o) {				
					me.onQuery();				
				}
			},
			'combo[id=filterByPrKind]':{//按照物料大类筛选
				beforerender: function(f) {
					var me=this;
					me.BaseUtil.getSetting('MakeMaterial!issue', 'filterByPrKind', function(v) {
						if(v){
							f.show();	
						}
					});
				},
				change: function(field,n,o) {				
					me.onQuery();				
				}
			},
	
		});
	},
	turnOut: function(grid) {
		var me = this,
		material = this.getEffectData(grid.selModel.getSelection());
		if (material.length > 0) {
			grid.setLoading(true); //loading...
			Ext.Ajax.request({
				url: basePath + 'pm/make/turnOut.action',
				params: {
					data: Ext.encode(material),
					wh: Ext.getCmp('whcode').checked,
					whman: Ext.getCmp('pr_whmancode').value,
					caller: caller,
					type: 'MAKE'
				},
				method: 'post',
				callback: function(options, success, response) {
					grid.setLoading(false);
					var localJson = new Ext.decode(response.responseText);
					if (localJson.exceptionInfo) {
						showError(localJson.exceptionInfo);
					}
					if(localJson.log.indexOf("转入成功") > -1){
		   				showMessage('提示', localJson.log);
		   				me.onQuery();
		   				if (localJson.success) {
							turnSuccess(function() {
								grid.selModel.deselectAll(true);
								grid.multiselected = new Array();
							});
					    }
		   			}else{
		   				showError(localJson.log);
		   			}		   													
				}
			});
		}
	},
	turnProdIOBC: function(grid) {
		var me = this,
		material = this.getEffectData(grid.selModel.getSelection());
		if (material.length > 0) {
			Ext.create('Ext.window.Window', {
				width: 350,
				height: 185,
				id: 'win',
				title: '<h1>生成拨出单</h1>',
				layout: 'column',
				items: [{
					margin: '10 0 0 0',
					xtype: 'dbfindtrigger',
					fieldLabel: '拨入仓库编号',
					name: 'wh_code',
					value: '',
					listeners: {
						aftertrigger: function(t, d) {
							t.ownerCt.down('textfield[name=wh_code]').setValue(d.get('wh_code'));
							t.ownerCt.down('textfield[name=inwhname]').setValue(d.get('wh_description'));
						}
					}
				},
				{
					margin: '3 0 0 0',
					xtype: 'textfield',
					fieldLabel: '拨入仓库名称',
					readOnly: true,
					name: 'inwhname',
					value: ''
				}],
				buttonAlign: 'center',
				buttons: [{
					xtype: 'button',
					columnWidth: 0.12,
					text: '确定',
					width: 60,
					iconCls: 'x-button-icon-save',
					handler: function(btn) {
						var inwhcode = btn.ownerCt.ownerCt.down('dbfindtrigger[name=wh_code]').value;
						if (!inwhcode) {
							showError('请先填写拨入的仓库编号!');
							return;
						} else {
							grid.setLoading(true); //loading... 
							Ext.Ajax.request({
								url: basePath + 'pm/make/turnProdIOBC.action',
								params: {
									data: Ext.encode(material),
									inwhcode: inwhcode,
									whmancode: Ext.getCmp('pr_whmancode').value
								},
								method: 'post',
								callback: function(options, success, response) {
									grid.setLoading(false);
									var localJson = new Ext.decode(response.responseText);
									if (localJson.exceptionInfo) {
										showError(localJson.exceptionInfo);
									}
									if (localJson.log) {
										showMessage('提示', localJson.log);
										return;
									}
									if (localJson.success) {
										turnSuccess(function() {
											grid.selModel.deselectAll(true);
											grid.multiselected = new Array();
										});
									}
									Ext.getCmp('win').close();
									me.onQuery();
								}
							});

						}
					}
				},
				{
					xtype: 'button',
					columnWidth: 0.1,
					text: '取消',
					width: 60,
					iconCls: 'x-button-icon-close',
					margin: '0 0 0 10',
					handler: function(btn) {
						Ext.getCmp('win').close();
					}
				}]
			}).show();
		}
	},
	/**
	 * 筛选
	 */
	onQuery: function() {
		var me = this, grid = Ext.getCmp('grid');
		var  c = this.getMixedGroups(grid.getStore().data.items, ['ma_code','ma_id']);
        if(grid.getStore().getCount() != 0 && (c.length != grid.getStore().getCount())){
    			showError('筛选的单据编号重复');
    			return ;
    	 }
    	 
		grid.selModel.deselectAll(true);
		grid.multiselected = new Array();
		//计算thisqty
		var bool = this.calThisQty(grid);
		if (!bool) return;
		//Query
		var condition = null,dirtyrecords=new Array();
		Ext.each(grid.store.data.items,function(item) {
			if (item.data['ma_code'] != null && item.data['ma_code'] != '') {
				if (item.data['ma_id']==null || item.data['ma_id']==''){
					showError('制造单号'+item.data['ma_code']+'必须从放大镜选择');   
				}else{
					if (condition == null) {
						condition = "(mm_code='" + item.data['ma_code'] + "'";
					} else {
						condition += " OR mm_code='" + item.data['ma_code'] + "'";
					}
				} 
			}else if(item.dirty){
				dirtyrecords.push(item);
			}
			
		});
		if(condition == null){
			condition = " 1=2 ";//未录入有效工单，则不筛选任何数据
		}else{
			condition +=  ")";
		}
		if(dirtyrecords.length>0) grid.store.remove(dirtyrecords);
		if (Ext.getCmp('pr_whmancode')) {
			var whmancode = Ext.getCmp('pr_whmancode');
			if (whmancode && whmancode.value != '') {
				if (Ext.getCmp('ifnullwhman').checked) {
					condition += "and (pr_whmancode='" + whmancode.value + "' or NVL(pr_whmancode,' ')=' ')";
				} else {
					condition += "and pr_whmancode='" + whmancode.value + "'";
				}
			}
		}
		if (Ext.getCmp('pr_location')) {
			var location = Ext.getCmp('pr_location');
			if (location && location.value != '') {
				if (Ext.getCmp('ifnulllocation').checked) {
					condition += "and (pr_location like '%" + location.value + "%' or NVL(pr_location,' ')=' ')";
				} else {
					condition += "and pr_location like '%" + location.value + "%'";
				}
			}

		}
		if (Ext.getCmp('wccode')) {
			var wccode = Ext.getCmp('wccode');
			if (wccode && wccode.value != '') {
				condition += " and mm_wccode like '%" + wccode.value + "%' ";
			}
		}
		if(Ext.getCmp('groupPurs')){
			var grouppurs = Ext.getCmp('groupPurs');
			if(grouppurs && grouppurs.value != ''){ 
				condition += " and "+ grouppurs.value;
			}
		}
		if(Ext.getCmp('st_code')){
			var stepcode = Ext.getCmp('st_code');
			if(stepcode && stepcode.value != ''){
				condition += " and mm_stepcode like '%"+ stepcode.value+"%' " ;
			}
		}
		var filterByPrKind = Ext.getCmp('filterByPrKind');//显示剩余需要领料数为0的物料
		if(filterByPrKind && !filterByPrKind.hidden){
			var va = filterByPrKind.value;
			if(va!='' &&  va!=null && va!='全部'){
			   condition +=" and pr_kind='"+va+"' ";
			}
		}
		if (condition != null) {
			grid.busy = true;
			var dg = Ext.getCmp('editorColumnGridPanel');
			dg.selModel.deselectAll(true);
			dg.busy = true;
			
			var zeroQty = Ext.getCmp('showZeroQty');//显示剩余需要领料数为0的物料
			if(zeroQty && !zeroQty.hidden && zeroQty.checked){
				condition += " AND mm_qty>0 ";
			}else{
				condition += " AND (mm_qty-nvl(mm_totaluseqty,0)-(nvl(mm_havegetqty,0)+nvl(mm_returnmqty,0)-nvl(mm_addqty,0)) > 0)";
			}
				
			this.BaseUtil.getSetting('sys', 'usingMakeCraft', function(val){//启用车间作业
				if(!val){
					condition +=" AND (nvl(mm_materialstatus,' ')=' ') ";
				}
			});
			var urlcondition = getUrlParam('urlcondition');
			condition = urlcondition != null ? condition + " and " + urlcondition: condition;
			this.BaseUtil.getSetting('MakeMaterial!issue', 'ifDisplayPull', function(val){
				if(!val){
					condition=condition+" and NVL(pr_supplytype,' ')<>'PULL' ";
				}
				//修改成这种方式可以减少render 时间，以前的方式先加载主料render,在逐条插入替代料，
				//每插入一次替代料就所有数据render一次，到时render浪费很多时间
				dg.reloadData(condition + ' order by mm_maid,mm_detno', function(gridData){
					me.showReplace(condition,function(repData){
						dg.store.loadData(me.mergeRepData(gridData, repData));
						dg.store.fireEvent('load', dg.store);
					    dg.fireEvent('storeloaded', dg);
					});
				});
			   
				setTimeout(function() {
					dg.busy = false;
					grid.busy = false;
				},
				1000);
			}); 
		}
	},
	/**
	 * 计算可领料数
	 **/
	calThisQty: function(grid) {
		var items = grid.store.data.items,
		idx = new Array();
		Ext.each(items,
				function(item) {
			if (item.data['ma_code'] != null && item.data['ma_code'] != '') {
				idx.push(item.data['ma_id']);
			}
		});
	
		var bool = true;
		if (idx.length > 0) {
			Ext.Ajax.request({
				url: basePath + 'pm/make/calThisQty.action',
				async: false,
				params: {
					ids: Ext.Array.concate(idx, ','),
					caller:caller
				},
				callback: function(opt, s, r) {
					var res = Ext.decode(r.responseText);
					if (res.exceptionInfo) {
						showError(res.exceptionInfo);
						bool = false;
					}
				}
			});
		}
		return bool;
	},
	/**
	 * 替代料
	 */
	showReplace: function(condition,callback) {
		Ext.Ajax.request({
			url: basePath + 'common/getFieldsDatas.action',
			params: {
				caller: "MakeMaterialReplace left join MakeMaterial on mm_id=mp_mmid left join Product on mp_prodcode=pr_code left join productwh on ((nvl(mp_whcode,' ')=' ' and pw_whcode=mm_whcode) or pw_whcode=mp_whcode) and pw_prodcode=mp_prodcode",
				fields: 'mp_assignqty as mm_assignqty,mp_mmid,mp_detno,mp_thisqty,mp_canuseqty,mp_repqty,mp_haverepqty,mp_addqty,mp_returnmqty,mp_remark,mp_prodcode,pr_detail,pr_spec,pr_unit,pr_location,mp_whcode,pw_onhand,pr_whmancode,pr_kind,pr_whmanname',
				condition: condition + ' and (mp_thisqty > 0 or mm_thisqty>0)'
			},
			async: false,
			method: 'post',
			callback: function(options, success, response) {
				var localJson = new Ext.decode(response.responseText);
				if (localJson.exceptionInfo) {
					showError(localJson.exceptionInfo);
					return;
				}else if(localJson.success){
    				data = Ext.decode(localJson.data);
    			}
	   			callback && callback.call(null, data); 
			}
		});
	},
	/**
	 * 合并领料数据、替代料数据
	 */
	mergeRepData: function(gridData, repData) {
		var me = this, datas = [];
		Ext.Array.each(gridData, function(d, i){
			datas.push(d);
			Ext.Array.forEach(repData, function(r){
				if(d.mm_id == r.MP_MMID) {
					datas.push(me.parseRepData(d, r));
				}
			});
		});
		return datas;
	},
	/**
	 * replace data 替代料数据 转成普通格式的grid data
	 */
	parseRepData: function(gridItem, repItem) {
		return ifCanrepqty?{
			mm_prodcode: repItem.MP_PRODCODE,
			mm_oneuseqty: gridItem.mm_oneuseqty,
			mm_code: gridItem.mm_code,
			pr_detail: repItem.PR_DETAIL,
			pr_spec: repItem.PR_SPEC,
			pr_unit: repItem.PR_UNIT,
			mm_canuserepqty: repItem.MP_CANUSEQTY,
			mm_qty:  gridItem.mm_qty,
			mm_thisqty: gridItem.mm_thisqty,
			mm_totaluseqty: gridItem.mm_totaluseqty,
			mm_havegetqty: gridItem.mm_havegetqty,
			mm_returnmqty: gridItem.mm_returnmqty,
			mm_addqty: gridItem.mm_addqty,
			mm_repqty:gridItem.mm_repqty,
			mm_ifrep: 1,
			mm_remark: repItem.MP_REMARK,
			mm_whcode: (repItem.MP_WHCODE== null||repItem.MP_WHCODE== '') ? gridItem.mm_whcode : repItem.MP_WHCODE,
			mm_detno: repItem.MP_DETNO,
			mm_id: repItem.MP_MMID,
			ma_vendcode:gridItem.ma_vendcode,
			isrep: true,
			pr_location: repItem.PR_LOCATION,
			pw_onhand:repItem.PW_ONHAND,
			pr_whmancode:repItem.PR_WHMANCODE,
			pr_kind:repItem.PR_KIND,
			mm_assignqty:repItem.MM_ASSIGNQTY,
			pr_whmanname:repItem.PR_WHMANNAME
		}:{
			mm_prodcode: repItem.MP_PRODCODE,
			mm_oneuseqty: gridItem.mm_oneuseqty,
			mm_code: gridItem.mm_code,
			pr_detail: repItem.PR_DETAIL,
			pr_spec: repItem.PR_SPEC,
			pr_unit: repItem.PR_UNIT,
			mm_canuserepqty: repItem.MP_CANUSEQTY,
			mm_qty: repItem.MP_CANUSEQTY,
			mm_thisqty: repItem.MP_THISQTY,
			mm_totaluseqty: repItem.MP_REPQTY,
			mm_havegetqty: repItem.MP_HAVEREPQTY,
			mm_returnmqty: repItem.MP_RETURNMQTY,
			mm_addqty: repItem.MP_ADDQTY,
			mm_ifrep: 1,
			mm_remark: repItem.MP_REMARK,
			mm_whcode: (repItem.MP_WHCODE== null||repItem.MP_WHCODE== '') ? gridItem.mm_whcode : repItem.MP_WHCODE,
			mm_detno: repItem.MP_DETNO,
			mm_id: repItem.MP_MMID,
			ma_vendcode:gridItem.ma_vendcode,
			isrep: true,
			pr_location: repItem.PR_LOCATION,
			pw_onhand:repItem.PW_ONHAND,
			pr_whmancode:repItem.PR_WHMANCODE,
			pr_whmanname:repItem.PR_WHMANNAME,
			pr_kind:repItem.PR_KIND,
			mm_assignqty:repItem.MM_ASSIGNQTY
		};
	},
	/**
	 * 转领料前，校验发料套数与领料数
	 */
	checkQty: function(a, b) {
		var err='' ;
		var c = this.getMixedGroups(b.selModel.getSelection(), ['mm_code', 'mm_id']);
		//判断主料加替代料的和不能超过可领料数量，单独勾选替代料发料不了问题解决,hasmain 是否有勾选主料
		if(ifCanrepqty){//勾选了不考虑替代料可用数
			Ext.Array.each(c,function(i) {
			    var mainqty =0 ,totalqty = 0,detno,hasmain=false;
				Ext.Array.each(i.groups,function(j) {
					if(!j.get('isrep')){//主料
						mainqty = j.get('mm_qty') - (j.get('mm_havegetqty') - j.get('mm_addqty') + j.get('mm_returnmqty') )  - j.get('mm_totaluseqty');				
					    hasmain=true;
					}
					//计算该mm_id 领料之和
					totalqty +=j.get('mm_thisqty');
					detno = j.get('mm_detno');
				});
				if(!hasmain){//未勾选主料,只发替代料
					var j = i.groups[0];
					mainqty = j.get('mm_qty') - (j.get('mm_havegetqty') - j.get('mm_addqty') + j.get('mm_returnmqty') )  - j.get('mm_totaluseqty');								  
				}
				if(totalqty > mainqty){
					err += "工单号["+i.keys.mm_code+"]序号["+detno+"]本次领料数超过可领数("+mainqty+")\n";
				}			
			});
		}
		if ((!Ext.getCmp('set').value && err == '') || (Ext.getCmp('set').value && ifIncludingLoss && err == '')) {
			return '';
		}else if(!Ext.getCmp('set').value && err != ''){
			return err;
		}
		var code,
		count,
		q = 0,
		m = 0;
		a.store.each(function(d) {
			code = d.get('ma_code');
			if (!Ext.isEmpty(code)) {
				q = d.get('ma_thisqty');
				Ext.Array.each(c,
						function(i) {
					if (i.keys.mm_code == code) {
						count = 0;
						m = 0;
						Ext.Array.each(i.groups,
								function(j) {
							if (m == 0) m = j.get('mm_oneuseqty');
							count += j.get('mm_thisqty');
						});
						if (Math.round(count - Number(q * m), 5) > 0.0001) {
							err += '领料数超出发料套数，工单号[' + code + ']';
						}
					}
				});
			}
		});
		//判断主料加替代料总数是否
		return err;
	},
	getMixedGroups: function(items, fields) {
		var data = new Object(),
		k,
		o;
		Ext.Array.each(items,
				function(d) {
			k = '';
			o = new Object();
			Ext.each(fields,
					function(f) {
			  if(d.get(f) != " " && d.get(f) != 0){
				    k += f + ':' + d.get(f) + ',';
				   o[f] = d.get(f);
			   }
			});
			if (k.length > 0) {
				if (!data[k]) {
					data[k] = {
							keys: o,
							groups: [d]
					};
				} else {
					data[k].groups.push(d);
				}
			}
		});
		return Ext.Object.getValues(data);
	},
	check: function(items) {
		var e = '';
		Ext.Array.each(items,
				function(item) {
			if (Ext.isEmpty(item.get('mm_whcode'))) {
				e += '\n工单[' + item.get('mm_code') + '],行号[' + item.get('mm_detno') + ']仓库为空';
			}
			if (Ext.isEmpty(item.get('mm_thisqty'))) {
				e += '\n工单[' + item.get('mm_code') + '],行号[' + item.get('mm_detno') + ']领料数为空';
			}
		});
		return e;
	},
	getEffectData: function(items) {
		var d = new Array();
		Ext.Array.each(items,
				function(item) {
			if (item.get('mm_thisqty') != 0) {
				d.push({
					mm_detno: item.get('mm_detno'),
					mm_code: item.get('mm_code'),
					mm_id: (item.get('isrep') == null||item.get('isrep') == '') ? item.get('mm_id') : -item.get('mm_id'),
					mm_thisqty: item.get('mm_thisqty'),
					mm_whcode: item.get('mm_whcode'),
					mm_prodcode: item.get('mm_prodcode'),
					pr_whmancode:item.get("pr_whmancode"),
					pr_kind:item.get("pr_kind")
				});
			}
		});
		return d;
	}
})