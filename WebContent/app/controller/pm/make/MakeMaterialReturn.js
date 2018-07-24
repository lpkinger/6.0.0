Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakeMaterialReturn', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.RenderUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
           'pm.make.MakeMaterialReturn', 'core.grid.Panel5', 'common.editorColumn.GridPanel',
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
					me.turnReturn(grid);
				}
			},
			'erpGridPanel5':{
				edit:function(editor, e){
					if(e.field =='ma_thisqty'){
						me.onQuery();
					}
				}
			},
			'button[name=query]': {
				click: function(btn){
					me.onQuery();
				}
			},
			'checkbox[id=whcode]' : {
				afterrender : function(f) {
					me.BaseUtil.getSetting('MakeMaterial!Return', 'GroupWarehouse', function(bool) {
						f.setValue(bool);
                    });
				}
			},
			'combo[id=groupPurs]': {
				beforerender: function(f) {
					me.BaseUtil.getSetting('MakeMaterial!Return', 'isGroupPurc', function(v) {
						if(v){
							f.show();							
						}
					});
				}
			},
			'combo[id=prsupplytype]': {
				beforerender: function(f) {
					me.BaseUtil.getSetting('MakeMaterial!Return', 'isPrSupplyType', function(v) {
						if(v){
							f.show();							
						}
					});
				},
				change: function(field,n,o) {
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
					me.BaseUtil.getSetting('MakeMaterial!Return', 'Select!OS!issue', function(bool) {
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
			'numberfield[name=ma_thisqty]': {
				change: function(t,n,o){
					if(n>0){
						var record = Ext.getCmp('grid').selModel.getLastSelected();
						if(record.data.ma_remainqty>0)
							 record.set('ma_remainqty',0);
					}
				}
			},
			'numberfield[name=ma_remainqty]': {
				change: function(t,n,o){
					if(n>0){
						var record = Ext.getCmp('grid').selModel.getLastSelected();
						if(record.data.ma_thisqty>0)
						     record.set('ma_thisqty',0);
					}
				}
			},
			'checkbox[id=showouttoint]': {//是否显示水口料筛选条件
				beforerender: function(f) {
					me.BaseUtil.getSetting('MakeMaterial!Return','ShowOuttoint', function(v) {
						if(v){
							f.show();							
						}
					});
				},
				change: function(field,n,o) {				
					me.onQuery();				
				}
			}
		});
	},
	onQuery: function(){
		var me = this, grid = Ext.getCmp('grid');
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
			condition = "( 1=2 )";//未录入有效工单，则不筛选任何数据
		}else{
			condition +=  ") ";
		}
		if(Ext.getCmp('groupPurs')){
			var grouppurs = Ext.getCmp('groupPurs');
			if(grouppurs && grouppurs.value != ''){
				condition += " and "+ grouppurs.value;
			}
		}
		if(Ext.getCmp('prsupplytype')){
			var prsupplytype = Ext.getCmp('prsupplytype');
			if(prsupplytype && prsupplytype.value != ''){
				condition += " and "+ prsupplytype.value;
			}
		}
		var toint = Ext.getCmp('showouttoint');//水口料
		if(toint && !toint.hidden && toint.checked){
			condition += " and "+ 'nvl(pr_putouttoint,0)<>0';
		}else{
			condition += " and "+ 'nvl(pr_putouttoint,0)=0';
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
				condition += " AND (nvl(mm_materialstatus,' ')=' ') AND (mm_onlineqty > 0) ";
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
	turnReturn: function(grid){
		var me = this;		
		warnMsg("确定要生成退料单吗?", function(btn){
			if(btn == 'yes'){
				var material = me.getEffectData(grid.selModel.getSelection());
				if(material.length > 0){
					var toint = Ext.getCmp('showouttoint');  //是否启用水口料筛选条件
					grid.setLoading(true);//loading...
    				Ext.Ajax.request({
    			   		url : basePath + 'pm/make/turnIn.action',
    			   		params: {
    			   			data: Ext.encode(material),
    			   			wh: Ext.getCmp('whcode').checked,
    			   			caller: caller,
    			   			type: 'MAKE',
    			   			outtoint:(toint&&!toint.hidden)?toint.checked:false//水口料
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
			}
		});
	},
	/**
	 * 计算成套退料
	 **/
//	calBackQty: function(grid){
		
//		if(datax.length > 0) {
//			Ext.Ajax.request({
//				url : basePath + 'pm/make/calBackQty.action',
//				async: false,
//				params: {
//					data: Ext.encode(datax),
//					caller:caller
//				},
//				callback: function(opt, s, r){
//					var res = Ext.decode(r.responseText);
//					if(res.exceptionInfo) {
//						showError(res.exceptionInfo);
//					}
//				}
//			});
//		}
//	},
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
		var datax = new Array(),ma_thisqtyx = new Array();
		Ext.each(items, function(item){
			if(item.data['ma_code'] != null && item.data['ma_code'] != ''){
				var obj = new Object();
				obj["ma_id"]= item.data['ma_id'];
				obj["ma_thisqty"]= item.data['ma_thisqty'];
				datax.push(obj);
			}
		});
		if(idx.length > 0) {
			Ext.Ajax.request({
				url : basePath + 'pm/make/calOnlineQty.action',
				async: false,
				params: {
					ids: Ext.Array.concate(idx, ','),
					data: Ext.encode(datax)
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
		condition += " and (mp_haverepqty-NVL(mp_scrapqty,0)>0 )";
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		params: {
	   			caller: 'MakeMaterialReplace left join MakeMaterial on mm_id=mp_mmid left join Product on mp_prodcode=pr_code' + 
	   				' left join WareHouse on wh_code=mp_whcode',
	   			fields: 'mp_mmid,mp_detno,mp_thisqty,mp_canuseqty,mp_repqty,mp_haverepqty,mp_scrapqty,mm_backqty,mm_onlineqty,mp_remark,mp_prodcode,pr_detail,pr_spec,pr_unit,wh_code,mp_assignqty,pr_whmanname,pr_whmancode',
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
	 * 合并退料数据、替代料数据
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
			mm_turnscrapqty: repItem.MP_TURNSCRAPQTY,
			mm_assignqty:repItem.MP_ASSIGNQTY,
			pr_whmanname:repItem.PR_WHMANNAME,
			pr_whmancode:repItem.PR_WHMANCODE
		};
	},
	/**
	 * 转退料前，校验退料套数与明细退料数
	 */
	checkQty: function(a, b){
		var c = this.getMixedGroups(b.selModel.getSelection(), ['mm_code', 'mm_id']),
			code,count,q = 0,m = 0,err = '',backqty,h=0,s=0,t=0;
			//@zjh start
			var flag = Ext.getCmp("allowChangeAfterCom").value;
		a.store.each(function(d){
			code = d.get('ma_code');
			if(!Ext.isEmpty(code)) {
				q = d.get('ma_thisqty');
				Ext.Array.each(c, function(i) {
					if(i.keys.mm_code == code) {
						count = 0;
						m = 0;
						backqty = 0 ;
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
								err += '\n退料数大于结存数，序号[' + detno + ']';
							}
						}else{
							if(r-backqty < count ) {
								err += '\n退料数大于结存数，序号[' + detno + ']';
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
			if(Ext.isEmpty(item.get('mm_whcode'))) {
				e += '\n工单[' + item.get('mm_code') + '],行号[' + item.get('mm_detno') + ']仓库为空';
			}
//			if( item.get('mm_thisqty') == 0 || Ext.isEmpty(item.get('mm_thisqty'))) {
//				e += '\n工单[' + item.get('mm_code') + '],行号[' + item.get('mm_detno') + ']退料数为空或者为0';
//			} 
			if(Ext.isEmpty(item.get('mm_thisqty'))) {
				e += '\n工单[' + item.get('mm_code') + '],行号[' + item.get('mm_detno') + ']退料数为空';
			} 
			/*if (item.get('mm_ifrep')==1){//替代料本次退料不能大于已领料数-报废数
				if(item.get('mm_thisqty') > item.get('mm_havegetqty') - item.get('mm_scrapqty') ) {
					e += '\n工单[' + item.get('mm_code') + '],替代料[' + item.get('mm_prodcode') + ']退料数大于已领料数-报废数';
				}
			}else{
				if(item.get('mm_thisqty') > item.get('mm_havegetqty') - item.get('mm_haverepqty') ) {
					e += '\n工单[' + item.get('mm_code') + '],行号[' + item.get('mm_detno') + ']退料数大于主料已领料数';
				}
				
			} */
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