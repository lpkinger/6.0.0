Ext.QuickTips.init();
Ext.define('erp.controller.pm.outsource.MakeMaterialReturn', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.RenderUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
    views:[
           'pm.outsource.MakeMaterialReturn', 'core.grid.Panel5', 'common.editorColumn.GridPanel',
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
					me.BaseUtil.getSetting('MakeMaterial!OS!Return', 'GroupWarehouse.OS', function(bool) {
						f.setValue(bool);
                    });
                     me.BaseUtil.getSetting('MakeMaterial!OS!Return', 'showUserFactoryWh', function(v) {//物料分仓库存只显示登录用户所属工厂对应仓库库存信息
						var grid = Ext.getCmp('editorColumnGridPanel');
						grid.ifOnlyShowUserFactoryWh = v||false;					
					});
					me.BaseUtil.getSetting('sys', 'usingMakeCraft', function(val){//启用车间作业
					    me.usingMakeCraft = val;
					});
				}
			},
			'combo[id=groupPurs]': {
				beforerender: function(f) {
					me.BaseUtil.getSetting('MakeMaterial!OS!Return', 'isGroupPurc', function(v) {
						if(v){
							f.show();							
						}
					});
				}
			},
			'combo[id=prsupplytype]': {
				beforerender: function(f) {
					me.BaseUtil.getSetting('MakeMaterial!OS!Return', 'isPrSupplyType', function(v) {
						if(v){
							f.show();							
						}
					});
				},
				change: function(field,n,o) {				
					me.onQuery();				
				}
			},
			'dbfindtrigger[name=ma_code]':{
				aftertrigger:function(){
					var record = Ext.getCmp('grid').selModel.getLastSelected(); 
					record.set('ma_thisqty',0);
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
			'checkbox[id=allowChangeAfterCom]': {
				afterrender: function(f) {
					me.BaseUtil.getSetting('Make', 'allowChangeAfterCom', function(v) {
						if(v){
							f.setValue(v);						
						}
					});
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
			'erpEditorColumnGridPanel':{
				afterrender : function(f) {
					me.BaseUtil.getSetting('MakeMaterial!OS!Return', 'Select!OS!issue', function(bool) {
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
			'checkbox[id=showouttoint]': {//是否显示水口料筛选条件
				beforerender: function(f) {
					me.BaseUtil.getSetting('MakeMaterial!OS!Return','ShowOuttoint', function(v) {
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
		var grid = Ext.getCmp('grid');
		var  c = this.getMixedGroups(grid.getStore().data.items, ['ma_code','ma_id']);
        if(c.length != grid.getStore().getCount()){
    			showError('筛选的单据编号重复');
    			return ;
    	 }
		this.calOnlineQty(grid);
		//计算退料套数
//		this.calBackQty(grid);
		//Query
		var condition = null;
		grid.store.each(function(item){
			if(item.data['ma_code'] != null && item.data['ma_code'] != ''){
				if (item.data['ma_id']==null || item.data['ma_id']==''){
					showError('制造单号'+item.data['ma_code']+'必须从放大镜选择');   
				}else{
					if(condition == null){
						condition = "(mm_code='" + item.data['ma_code'] + "'";
					} else {
						condition += " OR mm_code='" + item.data['ma_code'] + "'";
					}
				} 
			}
		});		
		if(condition == null){
			condition = "( 1=2 )";//未录入有效工单，则不筛选任何数据
		}else{
			condition +=  ")";
		}
		if(Ext.getCmp('groupPurs')){
			var grouppurs = Ext.getCmp('groupPurs');
			if(grouppurs && grouppurs.value != ''){
				condition += " and "+ grouppurs.value ;
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
		if(condition != null){
			grid.busy = true;
			grid.multiselected = new Array();
			var dg = Ext.getCmp('editorColumnGridPanel');
			dg.selModel.deselectAll(true);
			dg.busy = true;
			var allow = Ext.getCmp('allowChangeAfterCom').value;
			if(allow){
				condition += " AND (NVL(mm_havegetqty, 0) - NVL(mm_scrapqty, 0) - nvl(mm_backqty,0)-NVL(mm_turnscrapqty,0)) > 0 ";
			}else{
				condition += " AND (mm_onlineqty > 0) ";
			}
			var me = this;
		    if(!me.usingMakeCraft ){
				condition +=" AND (nvl(mm_materialstatus,' ')=' ') ";
			}
			dg.reloadData(condition + ' order by mm_maid,mm_detno', function(gridData){
				me.getReplaceData(condition, function(repData){
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
    			   			type: 'OS',
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
//		var items = grid.store.data.items, datax = new Array(),ma_thisqtyx = new Array();
//		Ext.each(items, function(item){
//			if(item.data['ma_code'] != null && item.data['ma_code'] != ''){
//				var obj = new Object();
//				obj["ma_id"]= item.data['ma_id'];
//				obj["ma_thisqty"]= item.data['ma_thisqty'];
//				datax.push(obj);
//			}
//		});
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
		var idx = new Array();
		grid.store.each(function(item){
			if(item.data['ma_code'] != null && item.data['ma_code'] != ''){
				idx.push(item.data['ma_id']);
				if (item.data['ma_id']==null || item.data['ma_id']==''){
					showError('制造单号'+item.data['ma_code']+'必须从放大镜选择');
					return;
				}
			}
		});
		var items = grid.store.data.items, datax = new Array(),ma_thisqtyx = new Array();
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
	getReplaceData: function(condition, callback) {
		condition += " and (mp_haverepqty-NVL(mp_scrapqty,0)>0 )";
		Ext.Ajax.request({
	   		url : basePath + 'common/getFieldsDatas.action',
	   		params: {
	   			caller: 'MakeMaterialReplace left join MakeMaterial on mm_id=mp_mmid left join Product on mp_prodcode=pr_code' + 
	   				' left join WareHouse on wh_code=mp_whcode',
	   			fields: 'mp_mmid,mp_detno,mp_thisqty,mp_canuseqty,mp_repqty,mp_haverepqty,mp_backqty,mp_scrapqty,mm_onlineqty,mm_backqty,mp_remark,mp_prodcode,pr_detail,pr_spec,pr_unit,wh_code,pr_whmancode,pr_whmanname',
	   			condition: condition
	   		},
	   		method : 'post',
	   		callback : function(opt, s, resp){
	   			var res = new Ext.decode(resp.responseText);
	   			var data = [];
	   			if(res.exceptionInfo){
	   				showError(res.exceptionInfo);
	   			} else if(res.success){
    				data = Ext.decode(res.data);
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
			ma_vendcode: gridItem.ma_vendcode,
			ma_apvendcode:gridItem.ma_apvendcode,
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
			mm_id: repItem.MP_MMID,//负数，表示替代料
			isrep: true,
			mm_havegetqty:repItem.MP_HAVEREPQTY,  
			mm_scrap:repItem.MP_SCRAPQTY,
			mm_backqty: repItem.MM_BACKQTY,
			mm_onlineqty: repItem.MM_ONLINEQTY,
			pr_whmancode:repItem.PR_WHMANCODE,
			pr_whmanname:repItem.PR_WHMANNAME
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
			   if(d.get(f) != null && d.get(f) != ''){
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
			if(Ext.isEmpty(item.get('mm_whcode'))) {
				e += '\n工单[' + item.get('mm_code') + '],行号[' + item.get('mm_detno') + ']仓库为空';
			}
			if( Ext.isEmpty(item.get('mm_thisqty'))) {
				e += '\n工单[' + item.get('mm_code') + '],行号[' + item.get('mm_detno') + ']退料数为空';
			} 
			/*因为已经有render限制
			 * if (item.get('mm_ifrep')==1){//替代料本次退料不能大于已领料数-报废数
				if(item.get('mm_thisqty') > item.get('mm_havegetqty')  ) {
					e += '\n工单[' + item.get('mm_code') + '],替代料[' + item.get('mm_prodcode') + ']退料数大于已领料数';
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
					mm_id: item.get('isrep') == null ? item.get('mm_id') : -item.get('mm_id'),
					mm_thisqty: item.get('mm_thisqty'),
					mm_whcode: item.get('mm_whcode'),
					ma_vendcode: item.get('ma_vendcode'),
					ma_apvendcode: item.get('ma_apvendcode') == null?"":item.get('ma_apvendcode')
				});
			}
		});
		return d;
	}
});