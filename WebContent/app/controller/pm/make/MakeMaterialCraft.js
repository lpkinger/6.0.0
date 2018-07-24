Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakeMaterialCraft', {
	extend: 'Ext.app.Controller',
	requires: ['erp.util.RenderUtil', 'erp.util.GridUtil', 'erp.util.BaseUtil'],
	views: ['pm.make.MakeMaterialCraft','pm.make.MakeMaterialCraftForm', 
	        'common.editorColumn.GridPanel', 'core.grid.YnColumn', 'core.button.CreateDetail', 'core.button.PrintDetail', 'core.trigger.DbfindTrigger',
	        'core.trigger.TextAreaTrigger','core.form.FtField','core.trigger.MultiDbfindTrigger'],
	init: function() {
		var me = this;
		me.GridUtil = Ext.create('erp.util.GridUtil');
		me.BaseUtil = Ext.create('erp.util.BaseUtil'); 
		this.control({ 
			/*'checkbox[id=wh]': {
				afterrender: function(f) {
					me.BaseUtil.getSetting(caller, 'groupWarehouse', function(v) {					
						f.setValue(v);
					});										
				}
			},*/
			'erpMakeMaterialCraftFormPanel':{
				/*afterrender: function(f) {
					me.BaseUtil.getSetting('Make!Base', 'addNotConsiderBalance', function(v) {
						addNotBalance  = v;					
					});
				}*/
			},
			'checkbox[id=addNotBalance]': {
				afterrender: function(f) {
					me.BaseUtil.getSetting('Make!Base', 'addNotConsiderBalance', function(v) {					
						f.setValue(v);
					});										
				}
			},
			'#query':{//筛选
				click:function(){
					me.onQuery();
				}
			},
			'field[name=mm_mdcode]':{
				beforetrigger: function(field) {
					var code = Ext.getCmp('ma_code').value;
					if (code != null && code != '') {
							code=code.replace(/#/g,"','");
						 field.dbBaseCondition = "mc_makecode in ('"+code+"')";
					}else{
						field.dbBaseCondition = "";
					}
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
    		'erpEditorColumnGridPanel':{
				storeloaded:function(grid){console.log('erpEditorColumnGridPanel->storeloaded');
					//zhouy  没有找到更好的解决 锁定列与normalview对不齐的方式  暂时这样处理
					Ext.defer(function(){
						var lockedView = grid.view.lockedView;
						if(lockedView){
							var tableEl = lockedView.el.child('.x-grid-table');
							if(tableEl){
								tableEl.dom.style.marginBottom = '7px';
							}
						} 
					}, 100);
				}	
			}
		});
	},
	/**
	 * 筛选
	 * @param 
	 */
	onQuery: function(){
		var me=this;
		if(caller=='MakeMaterialCraft!issue'){
			var bool = this.calThisQty();//计算可领料
			if (!bool) return;
		}else if (caller=='MakeMaterialCraft!Give'){
			this.calAddQty();//计算可补料
		}else if(caller=='MakeMaterialCraft!return'){
			this.calOnlineQty();//更新工单用料在线结存数量
		}else if(caller=='MakeMaterialCraft!Scrap'){
			this.calOnlineQty();
		}
		
		var dg = Ext.getCmp('editorColumnGridPanel');
		dg.multiselected = new Array();
		var form = Ext.getCmp('dealform');
		var condition = form.getCondition();//form 条件
		if(Ext.isEmpty(condition)) {
			cond = '1=1';
		} 
		var macode=Ext.getCmp('ma_code').value;
		if(!macode){
			showError('制造单号不能为空');
        	return;
		}
		var urlcondition = getUrlParam('urlcondition');//导航条件
		condition = urlcondition != null ? condition + " and " + urlcondition: condition;
		//判断是否填写本次领料数，制造单号不能为多个
		if(Ext.getCmp('ma_thisqty')){
			var ma_thisqty = Ext.getCmp('ma_thisqty').value;
			if(ma_thisqty>0){
				var ma_code = Ext.getCmp('ma_code').value;
				if(ma_code.indexOf("#")!=-1){
					showError('本次领料套数已填写，制造单号不能为多个！');
		        	return;
				}
			}
		}
		//仓管员以及是否包含空
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
		//储位以及是否包含空
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
		//工作中心
		/*if (Ext.getCmp('mm_wccode')) {
			console.log("sadas");
			var wccode = Ext.getCmp('mm_wccode');
			if (wccode && wccode.value != '') {
				condition += " and mm_wccode like '%" + wccode.value + "%' ";
			}
		}*/
		//集团采购料
		if(Ext.getCmp('groupPurs')){
			var grouppurs = Ext.getCmp('groupPurs');
			if(grouppurs && grouppurs.value == '是'){ 
				condition += " and pr_isgrouppurc<>0";
			}else if(grouppurs && grouppurs.value == '否'){
				condition += " and NVL(pr_isgrouppurc,0)=0";
			}
		}
		//工序编号
		if(Ext.getCmp('st_code')){
			var stepcode = Ext.getCmp('st_code');
			if(stepcode && stepcode.value != ''){
				condition += " and mm_stepcode like '%"+ stepcode.value+"%' " ;
			}
		}
		//按大类筛选
		if(Ext.getCmp("PK_NAME")){
			var PK_NAME = Ext.getCmp("PK_NAME");
			if(PK_NAME && PK_NAME.value!=''){
				condition +=" and pr_kind='"+va+"' ";
			}
		}
		//显示剩余需要领料数为0的物料
		var zeroQty = Ext.getCmp('zeroQty');
		if(zeroQty){
			if(zeroQty && zeroQty.checked){
				condition += " AND mm_qty>0 ";
			}else{
				condition += " AND (mm_qty-nvl(mm_totaluseqty,0)-(nvl(mm_havegetqty,0)+nvl(mm_returnmqty,0)-nvl(mm_addqty,0)) > 0)";
			}
		}
		//是否物料跳层（原启用车间作业）
		if(Ext.getCmp('mm_materialstatus')){
			var mm_materialstatus = Ext.getCmp('mm_materialstatus');
			if(!mm_materialstatus.value){ 
				condition +=" AND (nvl(mm_materialstatus,' ')=' ') ";
			}
		}
		//是否显示拉式物料
		if(Ext.getCmp("ifDisplayPull")){
			var dp= Ext.getCmp("ifDisplayPull");
			if(dp && dp.value!=''){
				condition=condition+" and NVL(pr_supplytype,' ')<>'PULL' ";
			}
		}
		//水口料
		if(Ext.getCmp('outtoint')){
			var toint = Ext.getCmp('outtoint');
			if(toint && toint.checked){
				condition += " and "+ 'nvl(pr_putouttoint,0)<>0';
			}else{
				condition += " and "+ 'nvl(pr_putouttoint,0)=0';
			}
		}
		
		//修改成这种方式可以减少render 时间，以前的方式先加载主料render,在逐条插入替代料，
		//每插入一次替代料就所有数据render一次，到时render浪费很多时间
		dg.reloadData(condition + ' order by mm_maid,mm_detno', function(gridData){
				dg.busy = true;
				dg.store.loadData(gridData);
				dg.store.fireEvent('load', dg.store);
				//dg.fireEvent('storeloaded', dg);
		});
	   
		setTimeout(function() {
			dg.busy = false;
		},
		1000);
		
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
		if(caller=='MakeMaterialCraft!issue'){
			Ext.Array.each(items,
					function(item) {
				if (Ext.isEmpty(item.get('mm_whcode'))) {
					e += '\n工单[' + item.get('mm_code') + '],行号[' + item.get('mm_detno') + ']仓库为空';
				}
				if (Ext.isEmpty(item.get('mm_thisqty'))) {
					e += '\n工单[' + item.get('mm_code') + '],行号[' + item.get('mm_detno') + ']领料数为空';
				}
			});
		}else if(caller=='MakeMaterialCraft!Give'){
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
				if(item.get('mm_thisqty') == 0 || Ext.isEmpty(item.get('mm_thisqty'))) {
					e += '\n工单[' + item.get('mm_code') + '],行号[' + item.get('mm_detno') + ']补料数为空或为0';
				}
			});
		}	
		return e;
	},
    vastDeal: function(url){
    	var me = this;
    	var grid =Ext.getCmp('editorColumnGridPanel');
    	var items=grid.selModel.getSelection();
    	var c = me.check(items);
		if (c.length > 0) {
			showError(c);
			return;
		}
    	if(items.length>0){
    		var material = me.getEffectData(grid,items);
        	if(material.length>0){
        		var params = new Object();
    			params.caller = caller;
        		params.data = unescape(Ext.JSON.encode(material).replace(/\\/g,"%"));
				var main = parent.Ext.getCmp("content-panel");
				main.getActiveTab().setLoading(true);//loading...
				Ext.Ajax.request({
			   		url : basePath + url,
			   		params: params,
			   		method : 'post',
			   		timeout: 6000000,
			   		callback : function(options,success,response){
			   			main.getActiveTab().setLoading(false);
			   			me.dealing = false;
			   			var localJson = new Ext.decode(response.responseText);
			   			if(localJson.exceptionInfo){
			   				var str = localJson.exceptionInfo;			   				
			   				if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){			   					
			   					str = str.replace('AFTERSUCCESS', '');	
			   					grid.multiselected = new Array();
			   					Ext.getCmp('dealform').onQuery();
			   				}
			   				showError(str);return;
			   			}
		    			if(localJson.success){
		    				if(localJson.log){
		    					showMessage("提示", localJson.log);
		    				}
		    				grid.multiselected = new Array();
		   					Ext.getCmp('dealform').onQuery();
			   			}
			   		}
				});
        	}else{
        		showError("没有需要处理的数据!");
        	}
    	}else{
    		showError("请勾选需要的明细!");
    	}
    },
    getEffectData: function(grid,items) {
		var d = new Array();
		Ext.Array.each(items,
				function(item) {
			if (item.get('mm_thisqty') != 0) {
				var o = new Object();
				if(grid.keyField){
					o[grid.keyField] = item.data[grid.keyField];
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
						var v = item.data[f];
						if(Ext.isDate(v)){
							v = Ext.Date.toString(v);
						}
						if(Ext.isNumber(v)){
							v = (v).toString();
						}
						o[f] = v;
					});
				}
				if((item.get('isrep') == null||item.get('isrep') == '')){
					o['mm_id'] =  item.get('mm_id');
				}else{
					o['mm_id'] = -item.get('mm_id');
				}
				d.push(o);
			}
		});
		return d;
	},
	/**
	 * 计算可领料数
	 **/
	calThisQty: function() {
		var ids =Ext.getCmp('ma_id').value,
		idx = new Array();
		var idx=ids.replace(/#/g, ',');		 
		var bool = true;
		if (idx.length > 0) {
			Ext.Ajax.request({
				url: basePath + 'pm/make/calThisQty.action',
				async: false,
				params: {
					ids: idx.toString()
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
	 * 计算可补料数
	 **/
	calAddQty: function(){
		var ids =Ext.getCmp('ma_id').value,
		idx = new Array();
		var idx=ids.replace(/#/g, ',');	
		if(idx.length > 0) {
			Ext.Ajax.request({
				url : basePath + 'pm/make/calAddQty.action',
				async: false,
				params: {
					ids: idx.toString()
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
	calOnlineQty:function(){
		var ids =Ext.getCmp('ma_id').value,
		idx = new Array();
		var idx=ids.replace(/#/g, ',');	
		if(idx.length > 0) {
			Ext.Ajax.request({
				url : basePath + 'pm/make/calOnlineQty.action',
				async: false,
				params: {
					ids: idx.toString()
				},
				callback: function(opt, s, r){
					var res = Ext.decode(r.responseText);
					if(res.exceptionInfo) {
						showError(res.exceptionInfo);
					}
				}
			});
		}
	}
});