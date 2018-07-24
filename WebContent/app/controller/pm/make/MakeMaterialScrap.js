Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakeMaterialScrap', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.RenderUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
      		'pm.make.MakeMaterialScrap', 'core.grid.Panel5', 'common.editorColumn.GridPanel', 'core.grid.YnColumn',
      		'core.button.CreateDetail', 'core.button.PrintDetail', 'core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger'
  	],
	init:function(){
		var me = this;
		me.GridUtil = Ext.create('erp.util.GridUtil');
	    me.BaseUtil = Ext.create('erp.util.BaseUtil');
		this.control({
			'button[id=retreat]': {
				click: function(btn){
					var grid = Ext.getCmp('editorColumnGridPanel');
					var e = me.checkQty(Ext.getCmp('grid'), grid);
					if(e.length > 0) {
						showError(e);return;
					}
					e = me.check(grid.selModel.getSelection());
					if(e.length > 0) {
						showError(e);return;
					}
					warnMsg("确定要生成报废单吗?", function(btn){
    					if(btn == 'yes'){
    						me.turnOut(grid);
    					}
    				});
				}
			},
			'button[name=query]': {
				click: function(btn){
					me.onQuery();
				}
			},
			'checkbox[id=allowChangeAfterCom]': {
				afterrender: function(f) {
					me.BaseUtil.getSetting('Make!Base', 'allowChangeAfterCom', function(v) {
						if(v){
							f.setValue(v);						
						}
					});
				}
			},
			'checkbox[id=whcode]' : {
				afterrender : function(f) {
					me.BaseUtil.getSetting('MakeMaterial!Scrap', 'GroupWarehouse', function(bool) {
						f.setValue(bool);
                    });
				}
			},
			'combo[id=groupPurs]': {
				beforerender: function(f) {
					me.BaseUtil.getSetting('MakeMaterial!Scrap', 'isGroupPurc', function(v) {
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
					me.BaseUtil.getSetting('MakeMaterial!Scrap', 'Select!OS!issue', function(bool) {
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
				}
			}
		});
	},
	turnOut: function(grid) {
		var me = this,
			material = this.getEffectData(grid.selModel.getSelection());
		if(material.length > 0){
			grid.setLoading(true);//loading...
			Ext.Ajax.request({
		   		url : basePath + 'pm/make/turnScrap.action',
		   		params: {
		   			data: Ext.encode(material),
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
		var me = this, grid = Ext.getCmp('grid');
		//计算thisqty
		this.calOnlineQty(grid);
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
		if(Ext.getCmp('groupPurs')){
			var grouppurs = Ext.getCmp('groupPurs');
			if(grouppurs && grouppurs.value != ''){
				condition += " and "+ grouppurs.value ;
			}
		}
		if(dirtyrecords.length>0) grid.store.remove(dirtyrecords);
		if(condition != null){
			grid.busy = true;
			grid.multiselected = new Array();
			var dg = Ext.getCmp('editorColumnGridPanel');
			dg.selModel.deselectAll(true);
			dg.busy = true;
			var allow = Ext.getCmp('allowChangeAfterCom').value;
			if(allow){
				condition += " AND (nvl(mm_materialstatus,' ')=' ') AND (NVL(mm_havegetqty, 0) - NVL(mm_scrapqty, 0) - nvl(mm_backqty,0)-NVL(mm_turnscrapqty,0)) > 0 ";
			}else{
				condition += " AND (nvl(mm_materialstatus,' ')=' ') AND (mm_thisqty > 0)";
			}
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
	 * 更新工单用料在线结存数量
	 **/
	calOnlineQty: function(grid){
		var items = grid.store.data.items, idx = new Array();
		Ext.each(items, function(item){
			if(item.data['ma_code'] != null && item.data['ma_code'] != ''){
				idx.push(item.data['ma_id']);
				if (item.data['ma_id']==null || item.data['ma_id']==''){
					showError('制造单号'+item.data['ma_code']+'必须从放大镜选择');
					return;
				}
			}
			
		});
		if(idx.length > 0) {
			Ext.Ajax.request({
				url : basePath + 'pm/make/calOnlineQty.action',
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
		condition += " and (mp_thisqty>0 and mp_haverepqty-NVL(mp_scrapqty,0)>0 )";
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		params: {
	   			caller: 'MakeMaterialReplace left join MakeMaterial on mm_id=mp_mmid left join Product on mp_prodcode=pr_code' + 
	   				' left join WareHouse on wh_code=mp_whcode',
	   			fields: 'mp_mmid,mp_detno,mp_thisqty,mp_canuseqty,mp_repqty,mp_scrapqty,mp_turnscrapqty,mp_haverepqty,mp_addqty,mm_onlineqty,mp_remark,mp_prodcode,pr_detail,pr_spec,pr_unit,wh_code',
	   			condition: condition
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
	 * 合并报废数据、替代料数据
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
			mm_whcode: repItem.WH_CODE,
			mm_detno: repItem.MP_DETNO,
			mm_id: repItem.MP_MMID,
			isrep: true,
			mm_havegetqty:repItem.MP_HAVEREPQTY,
			mm_backqty:repItem.MM_BACKQTY,
			mm_onlineqty:repItem.MM_ONLINEQTY,
			mm_scrap:repItem.MP_SCRAPQTY,
			mm_turnscrapqty: repItem.MP_TURNSCRAPQTY
		};
	},
	/**
	 * 转报废前，校验报废套数与报废数
	 */
	checkQty: function(a, b){
		var c = this.getMixedGroups(b.selModel.getSelection(), ['mm_code', 'mm_id']),
			code,count,q = 0,m = 0,err = '',backqty=0,h=0,s=0,t=0;
		//@zjh start
		var flag = Ext.getCmp("allowChangeAfterCom").value;
		a.store.each(function(d){
			code = d.get('ma_code');
			if(!Ext.isEmpty(code)) {
				Ext.Array.each(c, function(i) {
					if(i.keys.mm_code == code) {
						count = 0;
						m = 0;
						backqty=0;
						Ext.Array.each(i.groups, function(j){
							if(m == 0)
								detno = j.get('mm_detno');
								m = j.get('mm_oneuseqty');
								r = j.get('mm_onlineqty');
								backqty = j.get('mm_backqty');
								count += j.get('mm_thisqty');
								h = j.get('mm_havegetqty');
								s = j.get('mm_scrapqty');
								t = j.get('mm_turnscrapqty');
						});
						if(flag){
							if(h-s-backqty-t < count){
								err += '\n报废数大于结存数，序号[' + detno + ']';
							}
						}else{
							if(r-backqty < count ) {
								err += '\n报废数大于结存数，序号[' + detno + ']';
							}
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
				k += f + ':' + d.get(f) + ',';
				o[f] = d.get(f);
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
			/*if (item.get('mm_ifrep')==1){//替代料本次退料不能大于已领料数-报废数
				if(item.get('mm_thisqty') > item.get('mm_havegetqty') - item.get('mm_scrapqty') ) {
					e += '\n工单[' + item.get('mm_code') + '],替代料[' + item.get('mm_prodcode') + ']本次数大于已领料数-已报废数';
				}
			}else{
				if(item.get('mm_thisqty') > item.get('mm_havegetqty') - item.get('mm_haverepqty') ) {
					e += '\n工单[' + item.get('mm_code') + '],行号[' + item.get('mm_detno') + ']本次数大于主料已领料数';
				}
				
			} */
			if(Ext.isEmpty(item.get('mm_thisqty'))) {
				e += '\n工单[' + item.get('mm_code') + '],行号[' + item.get('mm_detno') + ']报废数未填写';
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
					mm_whcode: item.get('mm_whcode')
				});
			}
		});
		return d;
	}
});