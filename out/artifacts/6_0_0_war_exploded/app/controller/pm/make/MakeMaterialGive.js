Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakeMaterialGive', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.RenderUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'pm.make.MakeMaterialGive', 'core.grid.Panel5', 'common.editorColumn.GridPanel', 'core.grid.YnColumn',
      		'core.button.CreateDetail', 'core.button.PrintDetail', 'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger'
  	],
  	init:function(){
		var me = this;
		me.GridUtil = Ext.create('erp.util.GridUtil');
	    me.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.control({
			'button[id=create]': {
				click: function(btn){
					var grid = Ext.getCmp('editorColumnGridPanel');
					/*var e = me.checkQty(Ext.getCmp('grid'), grid);
					if(e.length > 0) {
						showError(e);return;
					}*/
					var e = me.check(grid.selModel.getSelection());
					if(e.length > 0) {
						showError(e);return;
					}
					warnMsg("确定要生成补料单吗?", function(btn){
    					if(btn == 'yes'){
    						me.turnAdd(grid);
    					}
    				});
				}
			},
			'button[name=query]': {
				click: function(btn){
					me.onQuery();
				}
			},
			'checkbox[id=whcode]' : {
				afterrender : function(f) {
					me.BaseUtil.getSetting('MakeMaterial!Give', 'GroupWarehouse', function(bool) {
						f.setValue(bool);
                    });
				}
			},
			'dbfindtrigger[name = mm_whcode]':{
				aftertrigger:function(f){
					var newvalue = f.value;
					var mm_id = f.record.data.mm_id;
					var isrep = f.record.data.isrep;
					var mpdetno = f.record.data.mm_detno;
					me.BaseUtil.getSetting('MakeMaterial!Give', 'changeWhCode', function(v) {
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
			'checkbox[id=ifnulllocation]': {
				afterrender: function(f) {
					me.BaseUtil.getSetting('MakeMaterial!Give', 'ifnulllocation', function(v) {
						f.setValue(v);
					});
				}
			},
			'combo[id=groupPurs]': {
				beforerender: function(f) {
					me.BaseUtil.getSetting('MakeMaterial!Give', 'isGroupPurc', function(v) {
						if(v){
							f.show();							
						}
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
					me.BaseUtil.getSetting('MakeMaterial!Give', 'Select!OS!issue', function(bool) {
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
			'dbfindtrigger[name=ma_code]':{
				aftertrigger:function(){
					var record = Ext.getCmp('grid').selModel.getLastSelected(); 
					record.set('ma_thisqty',0);
				},
				beforetrigger: function(f) {
					var wccode = getUrlParam("wccode");
    				if(wccode && wccode!='' && wccode!=null){
						f.dbBaseCondition = wccode;
    				}
				}
			},
			'combo[id=filterByPrKind]':{//按照物料大类筛选
				beforerender: function(f) {
					var me=this;
					me.BaseUtil.getSetting('MakeMaterial!Give', 'filterByPrKind', function(v) {
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
	turnAdd: function(grid) {
		var me = this,
			material = me.getEffectData(grid.selModel.getSelection());
		if(material.length > 0){		   
			grid.setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + 'pm/make/turnAdd.action',
		   		params: {
		   			data: Ext.encode(material),
		   			wh: Ext.getCmp('whcode').checked,
		   			caller: caller,
		   			type: 'MAKE'
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			grid.setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   			}
		   			if(localJson.log){
		   				showMessage('提示', localJson.log);
		   			}
	    			if(localJson.success){
	    				turnSuccess(function(){
	    					grid.multiselected = new Array();
	    				});
		   			}
	    			me.onQuery();
		   		}
			});
		}
	},
	/**
	 * 筛选
	 */
	onQuery: function(){
		var me = this , grid = Ext.getCmp('grid');
		var  c = this.getMixedGroups(grid.getStore().data.items, ['ma_code','ma_id']);
        if(grid.getStore().getCount() !=0 && (c.length != grid.getStore().getCount())){
    			showError('筛选的单据编号重复');
    			return ;
    	 }
		//计算thisqty
		this.calAddQty(grid);
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
		if (Ext.getCmp('pr_location')){
			var location=Ext.getCmp('pr_location'); 
			if (location && location.value!=''){ 
				if (Ext.getCmp('ifnulllocation').checked){
					condition+="and (pr_location like '%"+location.value+"%' or NVL(pr_location,' ')=' ')";
				}else{
					condition+="and pr_location like '%"+location.value+"%' ";
				}
			}			
		}
		if(Ext.getCmp('groupPurs')){
			var grouppurs = Ext.getCmp('groupPurs');
			if(grouppurs && grouppurs.value != ''){
				condition += " and "+ grouppurs.value ;
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
		if(condition != null){
			grid.multiselected = new Array();
			grid.busy = true;
			var dg = Ext.getCmp('editorColumnGridPanel');
			dg.selModel.deselectAll(true);
			dg.busy = true;
			condition += " AND (nvl(mm_materialstatus,' ')=' ') AND (mm_thisqty > 0)";
			//修改成这种方式可以减少render 时间，以前的方式先加载主料render,在逐条插入替代料，
			//每插入一次替代料就所有数据render一次，到时render浪费很多时间
			dg.reloadData(condition + ' order by mm_maid,mm_detno', function(gridData){
				me.showReplace(condition, function(repData){
					dg.store.loadData(me.mergeRepData(gridData, repData));
					dg.store.fireEvent('load', dg.store);
					dg.fireEvent('storeloaded', dg);
				});
			});
			
			setTimeout(function(){
				dg.busy = false;
				grid.busy = false;
			}, 1000);
		}
	},
	/**
	 * 计算可补料数
	 **/
	calAddQty: function(grid){
		var items = grid.store.data.items, idx = new Array();
		Ext.each(items, function(item){
			if(item.data['ma_code'] != null && item.data['ma_code'] != ''){
				idx.push(item.data['ma_id']);
			}
		});
		if(idx.length > 0) {
			Ext.Ajax.request({
				url : basePath + 'pm/make/calAddQty.action',
				async: false,
				params: {
					ids: Ext.Array.concate(idx, ',')
				},
				callback: function(opt, s, r){
					var res = Ext.decode(r.responseText);
					if(res.exceptionInfo) {
						showError(res.exceptionInfo);
					}
				}
			});
		}
	},
	/**
	 * 替代料
	 */
	showReplace: function(condition, callback){
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		params: {
	   			caller: 'MakeMaterialReplace left join MakeMaterial on mm_id=mp_mmid left join Product on mp_prodcode=pr_code left join productwh on pw_whcode=mp_whcode and pw_prodcode=mp_prodcode',
	   			fields: 'mp_assignqty as mm_assignqty,mp_mmid,mp_detno,mm_thisqty as mp_thisqty,mp_canuseqty,mp_repqty,mp_haverepqty,mm_turnaddqty,mp_remark,mp_prodcode,pr_detail,pr_spec,pr_unit,pr_location,mp_whcode,pw_onhand,pr_whmancode,pr_kind,pr_whmanname',
	   			condition: condition + ' and ( mm_thisqty>0)'
	   		},
	   		async: false,
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
	   				showError(localJson.exceptionInfo);return;
	   			}else if(localJson.success){
    				data = Ext.decode(localJson.data);
    			}
	   			callback && callback.call(null, data);   
	   		}
		});
	},
	/**
	 * 合并补料数据、替代料数据
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
		return {
			mm_prodcode: repItem.MP_PRODCODE,
			mm_oneuseqty: gridItem.mm_oneuseqty,
			mm_code: gridItem.mm_code,
			pr_detail: repItem.PR_DETAIL,
			pr_spec: repItem.PR_SPEC,
			pr_unit: repItem.PR_UNIT,
			mm_canuserepqty: repItem.MP_CANUSEQTY,
			mm_thisqty: repItem.MP_THISQTY,  
			mm_totaluseqty: repItem.MP_REPQTY,
			mm_ifrep: 1,
			mm_remark: repItem.MP_REMARK,
			mm_whcode: repItem.MP_WHCODE,
			mm_detno: repItem.MP_DETNO,
			mm_id: repItem.MP_MMID,
			isrep: true,
			mm_havegetqty:repItem.MP_HAVEREPQTY,
			mm_backqty:repItem.MM_BACKQTY,
			mm_onlineqty:repItem.MM_ONLINEQTY,
			mm_scrap:repItem.MP_SCRAPQTY,
			mm_turnscrapqty: repItem.MP_TURNSCRAPQTY,
			pr_whmancode:repItem.PR_WHMANCODE,
			pr_kind:repItem.PR_KIND,
			mm_assignqty:repItem.MM_ASSIGNQTY,
			pr_whmanname:repItem.PR_WHMANNAME
		};
	},
	/**
	 * 转补料前，校验发料套数与补料数
	 */
	checkQty: function(a, b){
		var c = this.getMixedGroups(b.selModel.getSelection(), ['mm_code', 'mm_id']),
			code,count,q = 0,m = 0,err = '';
		a.store.each(function(d){
			code = d.get('ma_code');
			if(!Ext.isEmpty(code)) {
				q = d.get('ma_thisqty');
				Ext.Array.each(c, function(i) {
					if(i.keys.mm_code == code) {
						count = 0;
						m = 0;
						Ext.Array.each(i.groups, function(j){
							if(m == 0)
								m = j.get('mm_oneuseqty');
							count += j.get('mm_thisqty');
						});
						if(q * m < count && m > 0) {
							err += '\n补料数超出补料套数，工单号[' + code + ']';
						}
					}
				});
			}
		});
		return err;
	},
	getMixedGroups: function(items, fields) {
		var data = new Object(),k,o;
		Ext.Array.each(items, function(d){
			k = '';
			o = new Object();
			Ext.each(fields, function(f){
			if(d.get(f) != " " && d.get(f) != 0){
				k += f + ':' + d.get(f) + ',';
				o[f] = d.get(f);
			  }
			});
			if(k.length > 0) {
				if(!data[k]) {
					data[k] = {keys: o, groups: [d]};
				} else {
					data[k].groups.push(d);
				}
			}
		});
		return Ext.Object.getValues(data);
	},
	check: function(items) {
		var e = '';
		Ext.Array.each(items, function(item){
			if((item.get('mm_ifrep') == 1 || item.get('mm_ifrep') == -1) && !item.get('isrep')) {
				var max = item.data['mm_scrapqty'] + item.data['mm_returnmqty'] - item.data['mm_balance']
	  				- item.data['mm_addqty']- item.data['mm_turnaddqty'],
	  				id = item.get('mm_id');
				var total = 0;
				Ext.each(items, function(){
					if(this.get('mm_id') == id)
						total += this.get('mm_thisqty');
				});
				if(total > max) {
					e += '\n工单[' + item.get('mm_code') + '],行号[' + item.get('mm_detno') + ']本次补料数+替代本次补料数超出总的可补料数.';
				}
			}
			if(Ext.isEmpty(item.get('mm_whcode'))) {
				e += '\n工单[' + item.get('mm_code') + '],行号[' + item.get('mm_detno') + ']仓库为空';
			}
			if( Ext.isEmpty(item.get('mm_thisqty'))) {
				e += '\n工单[' + item.get('mm_code') + '],行号[' + item.get('mm_detno') + ']补料数为空';
			}
		});
		return e;
	},
	getEffectData: function(items) {
		var d = new Array();
		Ext.Array.each(items, function(item){
			if (item.get('mm_thisqty') != 0) {
				d.push({
					mm_detno: item.get('mm_detno'),
					mm_code: item.get('mm_code'),
					mm_id: item.get('isrep') == null ? item.get('mm_id') : -item.get('mm_id'),
					mm_thisqty: item.get('mm_thisqty'),
					mm_whcode: item.get('mm_whcode'),
					pr_whmancode:item.get('pr_whmancode'),
					pr_kind:item.get("pr_kind")
				});
			}
		});
		return d;
	}
});