/**
 *  * grid render
 */
Ext.QuickTips.init();
Ext.define('erp.util.RenderUtil', {
	/**
	 * @PLM
	 */
	render_change: function(val, meta, record){
		if(record && record.data.percentdone < 30)
			return '<img src="'+basePath+'resource/images/renderer/remind2.png">'+'<span style="color:#436EEE;padding-left:2px">' + val + '</span>';
		else if(record && record.data.percentdone > 30 && record.data.percentdone < 50) 
			return '<img src="'+basePath+'resource/images/renderer/remind.png">'+'<span style="color:#5F9EA0;padding-left:2px">' + val + '</span>';
		else if(record && record.data.point == 100){
			return '<img src="'+basePath+'resource/images/renderer/award1.png">'+'<span style="color:blue;padding-left:2px">' + val + '</span>';
		}else if(record && record.data.point > 80){
			return '<img src="'+basePath+'resource/images/renderer/award2.png">'+'<span style="color:green;padding-left:2px">' + val + '</span>';
		}
		else return val;
	},
	/**
	 * @PLM
	 */
	plm_recordchange: function(val, meta, record){
		if(record && record.data.wr_percentdone == 100){
			if(record.data.wr_haveattach==1){
				return '<img src="'+basePath+'resource/images/renderer/finishrecord.png">'+'<span style="color:blue;padding-left:2px">' + val + '</span><img  style="padding-right:2px" src="../../resource/images/renderer/attach.png">';
			}
			else  return '<img src="'+basePath+'resource/images/renderer/finishrecord.png">'+'<span style="color:blue;padding-left:2px">' + val + '</span>';
		}
		else {
			if(record.data.wr_haveattach==1){
				return '<span style="color:green;padding-left:2px">' + val + '</span><img  align="right" style="padding-right:2px" src="'+basePath+'resource/images/renderer/attach.png">';
			}
			else  return '<span style="color:green;padding-left:2px">' + val + '</span>';
		}
	}, 
	ad_status:function(val,meta,record){
		if(val=='CLOSED')return '<span style="color:green">已处理</span>';
		else return '<span style="color:red;">未处理</span>';
	},
	/**
	 * @PLM
	 */
	plm_projectbudget:function(val,meta,record){
		if(record.data.cost>record.data.budget){
			return '<div class="color-column-inner" style="background-color:red" align="center">&nbsp;</div>';
		}else return  '<div class="color-column-inner" style="background-color:green" align="center">&nbsp;</div>';
	},
	plm_projectchange: function(val, meta, record){
		if(record && record.data.prjplan_prjname == 'ERP')
			return '<img src="'+basePath+'resource/images/renderer/important.png">'+'<span style="color:blue;padding-left:2px">' + val + '</span>';
		else return '<span style="color:green;padding-left:2px">' + val + '</span>';
	},
	plm_reduce: function(val, meta, record, x, y, store, view){
		var me = this.RenderUtil || this;
		var field = this.columns[y].dataIndex;
		if(me.args){
			var arg = me.args.plm_reduce[field];
			var maxValue = 0;
			if(arg && arg.length > 0){
				var v = 0;
				Ext.each(arg, function(a, index){
					if(Ext.isNumber(a)){
						v = a;
					} else {
						v = record.data[a] || 0;
					}
					if(index == 0){
						maxValue = Number(v);
					} else {
						maxValue -= Number(v);
					}
				});
			}
			val = (val == null || val == 0) ? maxValue : val;
			if(record.data[field] != val){
				record.set(field, val);
			}
			if(val > 0){
				return '<img src="' + basePath + 'resource/images/icon/need.png">' + 
				'<span style="color:blue;padding-left:2px">' + val + '</span>';
			} else if(val < 0){
				return 0;
			} else {
				return val; 
			}
		} else {
			return val;
		}
	},
	/**
	 * @PLM
	 */
	plm_resource_rank:function(val,meta,record){
		if(record&&record.data.percentdone>89){
			return '<img src="'+basePath+'resource/images/renderer/start1.png">'+'<img src="'+basePath+'resource/images/renderer/start1.png">'+'<img src="'+basePath+'resource/images/renderer/start1.png">'+'<span style="color:blue;padding-left:2px">' + val + '</span>';
		}else if(record&&record.data.percentdone>69&&record.data.percentdone<90){
			return '<img src="'+basePath+'resource/images/renderer/start1.png">'+'<img src="'+basePath+'resource/images/renderer/start1.png">'+'<span style="color:blue;padding-left:2px">' + val + '</span>';
		}else if(record&&record.data.percentdone>50&&record.data.percentdone<70){
			return '<img src="'+basePath+'resource/images/renderer/start1.png">'+'<img src="'+basePath+'resource/images/renderer/start2.png">'+'<span style="color:blue;padding-left:2px">' + val + '</span>';
		}
	},
	/**
	 *@PLM 
	 * */
	PLM_planstatus:function(val,meta,record){
		var statuscode=record.data['prjplan_statuscode'];
		if(statuscode=='FINISHED'){
			return '<img src="' + basePath + 'resource/images/renderer/finishrecord.png" >' + 
			'<span style="color:green;padding-left:2px;">' + val + '</span>';
		}else if(statuscode=='DOING'){
			return '<img src="' + basePath + 'resource/images/renderer/doing.png" >' + 
			'<span style="color:blue;padding-left:2px;">' + val + '</span>';
		}else return '<img src="' + basePath + 'resource/images/renderer/remind2.png" >'+'<span style="color:red;padding-left:2px;">' + val + '</span>';
	},
	PLM_MainTaskStatus:function(val,meta,record){
		var statuscode=record.data['handstatuscode'];
		if(statuscode=='FINISHED'){
			return '<img src="' + basePath + 'resource/images/renderer/finishrecord.png" >' + 
			'<span style="color:green;padding-left:2px;">' + val + '</span>';
		}else if(statuscode=='DOING'){
			return '<img src="' + basePath + 'resource/images/renderer/doing.png" >' + 
			'<span style="color:blue;padding-left:2px;">' + val + '</span>';
		}else if(statuscode=='ACTIVE'||statuscode=='STOP'){
			return '<img src="' + basePath + 'resource/images/renderer/key2.png" >' + 
			'<span style="color:red;padding-left:2px;" >' + val + '</span>';
		}else {
			return '<img src="' + basePath + 'resource/images/renderer/key1.png">'+'<span style="color:#8B8B83;padding-left:2px ">' + val + '<a/></span>';
		}
	},
	PLM_TaskStatus:function(val,meta,record){
		var statuscode=record.data['ra_statuscode'];
		if(statuscode=='FINISHED'){
			return '<img src="' + basePath + 'resource/images/renderer/finishrecord.png" >' + 
			'<span style="color:green;padding-left:2px;">' + val + '</span>';
		}else if(statuscode=='START'){
			return '<img src="' + basePath + 'resource/images/renderer/doing.png" >' + 
			'<span style="color:blue;padding-left:2px;">' + val + '</span>';
		}else if(statuscode=='STOP'){
			return '<img src="' + basePath + 'resource/images/renderer/key1.png">'+'<span style="color:#8B8B83;padding-left:2px ">' + val + '<a/></span>';
		}else {
			return '<img src="' + basePath + 'resource/images/renderer/key2.png">'+'<span style="color:#8B8B83;padding-left:2px ">' + val + '<a/></span>';
		}
	},
	PLM_MyTask:function(val,meta,record){
		var statuscode=record.data['ra_statuscode'];
		if(statuscode=='FINISHED'){
			return '<img src="' + basePath + 'resource/images/readed.png" >' + 
			'<span style="color:green;padding-left:2px;">' + val + '</span>';
		}else if(statuscode=='DOING'||statuscode=='START'){
			return '<img src="' + basePath + 'resource/images/renderer/doing.png" >' + 
			'<span style="color:blue;padding-left:2px;">' + val + '</span>';
		}else {
			return '<img src="' + basePath + 'resource/images/renderer/important.png">'+'<span style="color:blue;padding-left:2px ">' + val + '<a/></span>';
		}
	},		
	/**
	 *@PLM 
	 * */
	plm_BUGAttach:function(val,meta,record){
		if(record&&record.data.cld_attach!=null&&record.data.cld_attach!=""){
			var attach=record.data.cld_attach;
			//var path="dsd";
			// return  '<a href=' + basePath + 'common/downloadbyId.action>下载</a>';
			return '<img  align= style="padding-left:2px" src="'+basePath+'resource/images/renderer/attach.png"></span>'+val+'</br><a href="' + basePath + 'common/downloadbyId.action?id='+attach.split(";")[1]+'">' + attach.split(";")[0] + '</a>';
		}else return '<img src="' + basePath + 'resource/images/icon/need.png" title="'+val+'">' + 
		'<span style="color:blue;padding-left:2px;" title="'+val+'">' + val + '</span>';

	},
	plm_BUGTurn:function(val,meta,record){
		if(record&&record.data.cc_turn!=null&&record.data.cc_turn!=""){
			var turn=record.data.cc_turn;
			meta.style = "text-align:center";
			return '<sapn style="color:blue;padding-left:20px;">'+turn.split(";")[0]+'</span><img   style="padding-left:10px;padding-top:3px;padding-right:10px;" src="'+basePath+'resource/images/renderer/turn.png">'+turn.split(";")[1];
		}else return "";

	},
	/**
	 *@PLM 
	 * */
	plm_BUGStatus:function(val,meta,record){
		var statuscode=record.data.cld_statuscode?record.data.cld_statuscode:record.data.cbd_statuscode;
		if(statuscode=='PENDING'){
			return '<img src="' + basePath + 'resource/images/renderer/key2.png" title="'+val+'">' + 
			'<span style="color:red;padding-left:2px;" title="'+val+'">' + val + '</span>';
		}else if(statuscode=='TESTING'){
			return '<img src="' + basePath + 'resource/images/renderer/test.png" title="'+val+'">' + 
			'<span style="color:blue;padding-left:2px;" title="'+val+'">' + val + '</span>';
		}else if(statuscode=='HANDED'){
			return '<img src="' + basePath + 'resource/images/renderer/finishrecord.png" title="'+val+'">' + 
			'<span style="color:green;padding-left:2px;" title="'+val+'">' + val + '</span>';
		}else if(statuscode=='FINISH'){
			return '<img src="' + basePath + 'resource/images/renderer/finishrecord.png" title="'+val+'">' + 
			'<span style="color:green;padding-left:2px;" title="'+val+'">' + val + '</span>';
		}else if(statuscode=='NOTDEAL'){
			return '<img src="' + basePath + 'resource/images/renderer/key1.png" title="'+val+'">' + 
			'<span style="color:#8B8B83;padding-left:2px;" title="'+val+'">' + val + '</span>';
		}else if(statuscode=='HANDUP'){
			return '<img src="' + basePath + 'resource/images/renderer/key2.png" title="'+val+'">' + 
			'<span style="color:red;padding-left:2px;" title="'+val+'">' + val + '</span>';
		}
	},
	plm_BUGTest:function(val,meta,record){
		if(val&&val!=null){
			if(val=='-1'){
				return  '<span style="color:green;padding-left:2px;" title="'+val+'">测试通过</span>';
			}else if(val=='0'){
				return  '<span style="color:red;padding-left:2px;" title="'+val+'">测试失败</span>';
			}
		}else return null;
	},
	/**
	 *稽核查看单据信息 
	 * */
	SysCheckHref:function(val,meta,record){
		var url=null;
		if(record.data.scd_url.indexOf('?')>0){
			if(record.data.sf_mainfield){
				//主从记录都有
				url=record.data.scd_url+"&formCondition="+record.data.sf_keyfield+"IS"+record.data.scd_sourceid+"&gridCondition="+record.data.sf_mainfield+"IS"+record.data.scd_sourceid;
			}else url=record.data.scd_url+"&formCondition="+record.data.sf_keyfield+"IS"+record.data.scd_sourceid;
		}else {
			if(record.data.sf_mainfield){
				//主从记录都有
				url=record.data.scd_url+"?formCondition="+record.data.sf_keyfield+"IS"+record.data.scd_sourceid+"&gridCondition="+record.data.sf_mainfield+"IS"+record.data.scd_sourceid;
			}else url=record.data.scd_url+"?formCondition="+record.data.sf_keyfield+"IS"+record.data.scd_sourceid;
		}
		return '<a href="javascript:openUrl(\''+url+'\');">' + val + '</a>';
	},
	/**
	 *查看工作日程 
	 * */
	seeCalendar:function(val,meta,record){
		if(record.data.epd_emcode!=''&&record.data.epd_starttime!=null && record.data.epd_epid!=0){
			var time=Ext.Date.format(record.data.epd_starttime, 'Y-m-d');
			return "<input type='button' value='查看工作日程' name='detailbutton' style='color:gray;font-size:13px;cursor:pointer;height:25px;' onClick='Ext.getCmp(\"form\").seec(\""+record.data.epd_emcode+"\",\""+time+"\",\""+record.data.epd_epid+"\",\""+caller+"\")'>";
		}
		return '';
	},
	/**
	 * 生产计划链接
	 * */
	MakePlanHref:function(val,meta,record){
		var url='jsps/pm/make/makeBase.jsp?whoami=Make!Base';
		var code=record.data.ma_code;
		var keyValue=record.data.ma_id;
		url+='&formCondition=ma_idIS'+keyValue+'&gridCondition=mm_maidIS'+keyValue;
		return '<a href="javascript:openUrl(\''+url+'\');">' + code + '</a>';
	},
	/**
	 * 生产计划链接
	 * */
	MakeHref:function(val,meta,record){
		var url='jsps/pm/make/makeBase.jsp?whoami=Make';
		var code=record.data.ma_code;
		var keyValue=record.data.ma_id;
		url+='&formCondition=ma_idIS'+keyValue+'&gridCondition=mm_maidIS'+keyValue;
		return '<a href="javascript:openUrl(\''+url+'\');">' + code + '</a>';
	},
	/**
	 * 应付发票 付款详情
	 * */
	PayApbillHref:function(val,meta,record){
		var url='jsps/common/commonpage.jsp?whoami=PayAPBill';
		var code=record.data.ppdd_billcode;
		url+='&formCondition=ab_codeIS'+code+'&gridCondition=pa_codeIS'+code;
		return '<a href="javascript:openUrl(\''+url+'\');">' + code + '</a>';
	},
	/**
	 * 采购订单链接
	 * */
	PurchaseHref:function(val,meta,record){
		var url='jsps/scm/purchase/purchase.jsp';
		var code=record.data.vad_pucode;
		url+='?formCondition=pu_codeIS'+code+'&gridCondition=pd_codeIS'+code;
		return '<a href="javascript:openUrl(\''+url+'\');">' + code + '</a>';
	},
	/**
	 * 付款申请单 应付发票链接
	 * */
	ApbillHref:function(val,meta,record){
		var url='jsps/fa/ars/apbill.jsp?whoami=APBill!CWIM';
		var code=record.data.ppdd_billcode;
		url+='&formCondition=ab_codeIS'+code+'&gridCondition=abd_codeIS'+code;
		return '<a href="javascript:openUrl(\''+url+'\');">' + code + '</a>';
	},
	/**
	 * 预付款申请单 采购订单链接
	 * */
	PurcYFHref:function(val,meta,record){
		var url='jsps/scm/purchase/purchase.jsp';
		var code=record.data.ppdd_pucode;
		url+='?formCondition=pu_codeIS'+code+'&gridCondition=pd_codeIS'+code;
		return '<a href="javascript:openUrl(\''+url+'\');">' + code + '</a>';
	},
	/**
	 * 
	 * 检验单链接
	 * */
	IQCHref:function(val,meta,record){
		var url='jsps/scm/qc/verifyApplyDetail2.jsp?whoami=VerifyApplyDetail';
		var code=record.data.ve_code;
		url+='&formCondition=ve_codeIS'+code+'&gridCondition=ved_codeIS'+code;		
		return '<a href="javascript:openUrl(\''+url+'\');">' + code + '</a>';

	},
	/**
	 * 检验单链接 --第二个明细表只能传关联id
	 */
	newIQCHref:function(val,meta,record){
	if(!Ext.isEmpty(val)) {
		if(!window.__fn) {
			var fn = function(ve_code) {
				Ext.Ajax.request({
					url: basePath + 'common/getFieldData.action',
					params: {
						caller: 'QUA_VerifyApplyDetail',
						field: 've_id',
						condition: 've_code=\'' + ve_code + '\''
					},
					callback: function(opt, s, r) {
						if(s) {
							var rs = Ext.decode(r.responseText);
							if(rs.data != null && rs.data > 0) {
								openUrl('jsps/scm/qc/verifyApplyDetail2.jsp?whoami=VerifyApplyDetail&formCondition=ve_idIS' + rs.data + 
										'&gridCondition=ved_veidIS' + rs.data);
							}
						}
					}
				});
			};
			window.__fn = fn; 
		}
		val = '<a href="javascript:window.__fn(\'' + val + '\');">' + val + '</a>';
	}
	return val;
	},	
	
	/**
	 * 订阅项链接 SubsFormula
	 */
	SubsFHref:function(val,meta,record){
	if(!Ext.isEmpty(val)) {
		if(!window.__fn) {
			var fn = function(code_) {
				Ext.Ajax.request({
					url: basePath + 'common/getFieldData.action',
					params: {
						caller: 'SubsFormula',
						field: 'id_',
						condition: 'code_=\'' + code_ + '\''
					},
					callback: function(opt, s, r) {
						if(s) {
							var rs = Ext.decode(r.responseText);
							if(rs.data != null && rs.data > 0) {
								openUrl('jsps/common/subsformula.jsp?formCondition=id_IS' + rs.data + 
										'&gridCondition=formula_id_IS' + rs.data);
							}
						}
					}
				});
			};
			window.__fn = fn; 
		}
		val = '<a href="javascript:window.__fn(\'' + val + '\');">' + val + '</a>';
	}
	return val;
	},	
	
	/*
	 *点开链接
	 */
	Href: function(val, meta, record,x,y){
		var me = this.RenderUtil || this;
		var field = this.columns[y].dataIndex;
		var KeyValue = 0;
		var url='';
		var title='';
		var arg = me.args.Href[field];
		var keyField=arg[0];
		var data=record.data;
		if(arg && arg.length > 0){
			KeyValue=data[keyField];
			url=arg[1];
			title=arg[2];
		}
		if(val==''||val==$I18N.common.grid.emptyText) return val;
		if(keyField.indexOf('code')>=0){
			return '<a href="javascript:openFormUrl(\'' + KeyValue + '\',\''+keyField+'\',\''+url+'\',\''+title+'\''+ ');">' + val + '</a>';
		}
		return '<a href="javascript:openFormUrl(' + KeyValue + ',\''+keyField+'\',\''+url+'\',\''+title+'\''+ ');">' + val + '</a>';
	},
	/**
	 * 生产计划已分拆
	 * */
	MakePlanSplited:function(val,meta,record){
		if(record.data.ma_version){
			meta.tdCls = "x-grid-cell-renderer-cl";
		}

	},
	SysCheckStatus:function(val,meta,record){
		var statuscode=record.data.scd_statuscode;
		if(statuscode=='UNEXECUTE'){
			return '<img src="' + basePath + 'resource/images/icon/execute.png">' + 
			'<span style="color:red;padding-left:2px">' + val + '</span>';
		}else if(statuscode=='EXECUTE'){
			return '<img src="' + basePath + 'resource/images/renderer/finishrecord.png">' + 
			'<span style="color:green;padding-left:2px">' + val + '</span>';
		}else if(statuscode='FREEZE') return '<img src="' + basePath + 'resource/images/renderer/locked.png">' + 
		'<span style="color:blue;padding-left:2px">' + val+ '</span>';
		else return null;
	},
	SysCheckMethod:function(val,meta,record){
		var method=record.data.scd_method;
		if(method=='-1'){
			return  '<span style="color:red;padding-left:2px">扣分</span>';
		}else if(method=='0') return  '<span style="color:blue;padding-left:2px">提醒</span>';
		else return null;
	},
	SysCheckPunish:function(val,meta,record){
		var method=record.data.scd_method;
		meta.align='left';
		if(record.data.scd_ispunished=='-1'){
			return  '<span style="color:blue;padding-left:2px">已生成</span>';
		}else if(record.data.scd_ispunished=='0'&&method=='-1'){
			return  '<span style="color:green;padding-left:2px">未生成</span>';  
		}else return '<span style="color:#8B8B83;padding-left:2px">无</span>'; 
	},
	/**
	 * 显示所有信息
	 * */
	showAll:function(val,meta,record){
		if(val&&val!=null&&val.length>20){
			return val.substring(0,20)+'<button onClick="javascript:showAll(' +  '\''+val.replace(/\n/g,'<br>')+'\'' +');">详细</button>';
		}else return val;
	},
	/**
	 * 通过args传递参数到function 
	 */
	args: new Object(),
	/**
	 * 公式计算
	 * 通用方法
	 * @expression formula:pd_orderprice/(1+pd_taxrate/100)*pd_inqty
	 * 
	 */
	formula: function(val, meta, record, x, y, store, view){
	//如果出现除数为0则结果会是无穷大Infinity,出现无穷大则不显示
	if(val=='Infinity'||val=='-Infinity'){
		return "";
	}
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		var editvalue=val;
		if(!me || !me.args){
			grid = grid.ownerCt, me = grid.RenderUtil;
			if(!me || !me.args){
				return val;
			}
		}
		var arg = me.args.formula[field];
		if(arg && arg.length > 0){
			var data = record.data,keys = Ext.Object.getKeys(data),formu = arg[0];
			Ext.each(keys, function(k){
				if(contains(formu, k, true)) {
					formu = formu.replace(new RegExp(k,"gm"), '(' + data[k] + ')');
				}
			});
			var d = 0;
			try {
				d = eval(formu);
			} catch (e) {
				d = 0;
			}
			if(d == null || d == '' || String(d) == 'NaN' || String(d).length == 0)
				d = 0;
			var _val = val, _d = d,dic=2;
			if(column.format) {
			     dic=column.format.substr(column.format.indexOf('.')+1).length;
			     d=Ext.util.Format.round(_d,dic);//--d.toFixed(dic);
			     /*toFixed()方法时，规则并不是所谓的“四舍五入”而是四舍六入五取偶（又称四舍六入五留双）法，所谓“四舍六入五成双”，在百度百科上给的解释是：也即“4舍6入5凑偶”这里“四”是指≤4 时舍去，"六"是指≥6时进上，
			      * "五"指的是根据5后面的数字来定，当5后有数时，舍5入1；当5后无有效数字时，需要分两种情况来讲：①5前为奇数，舍5入1；②5前为偶数，舍5不进。（0是最小的偶数）
			      */
				_val = Ext.util.Format.number(val, column.format);
				_d = Ext.util.Format.number(d, column.format);
			}
			//如果出现除数为0则结果会是无穷大Infinity,出现无穷大则不显示
			if(_d=='In,fin,ity'||_d=='-In,fin,ity'){
				return "";
			}
			val = _val;
			//用原值赋值
			if(_val != _d) {
				val = d;
				record.set(field, d);
				val = _d;
			}				
		}
		if(record.get(field) != 0)
			return val;
		return "";
	},
	
	/**
	 * 通过args传递参数到function 
	 */
	args: new Object(),
	/**
	 * 公式计算
	 * 通用方法
	 * @expression formula:pd_orderprice/(1+pd_taxrate/100)*pd_inqty 
	 */
	_formula: function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		if(!me || !me.args){
			grid = grid.ownerCt, me = grid.RenderUtil;
			if(!me || !me.args){
				return val;
			}
		}
		var arg = me.args._formula[field];
		if(arg && arg.length > 0){
			var data = record.data,keys = Ext.Object.getKeys(data),formu = arg[0];
			Ext.each(keys, function(k){
				if(contains(formu, k, true)) {
					formu = formu.replace(new RegExp(k,"gm"), '(' + data[k] + ')');
				}
			});
			var d = 0;
			try {
				d = eval(formu);
			} catch (e) {
				d = 0;
			}
			if(d == null || d == '' || String(d) == 'NaN' || String(d).length == 0)
				d = 0;
			var fsize = (column.format && column.format.indexOf('.') > -1) ? 
					column.format.substr(column.format.indexOf('.') + 1).length : 2,
					_d = d.toFixed(fsize);
			//按设置的格式长度赋值
			if(val != _d) {
				record.set(field, _d);
				val = _d;
			}
			val = Ext.util.Format.number(val, column.format || '0,000.00');
		}
		if(record.get(field) != 0)
			return val;
		return "";
	},
	//信扬费用
	xyfee : function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		if(!me || !me.args){
			grid = grid.ownerCt, me = grid.RenderUtil;
			if(!me || !me.args){
				return val;
			}
		}
		if(val>record.modified['pi_feeexpensexy_user']){
			showError("请不要输入大于费用的最大值");
			val = record.modified['pi_feeexpensexy_user'];
			record.set('pi_feeexpensexy_user', val);
		}
		return val;
	},
	
	/**
	 * 公式计算
	 * 通用方法
	 * @expression formula:pd_orderprice/(1+pd_taxrate/100)*pd_inqty 
	 */
	overqty: function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		if(!me || !me.args){
			grid = grid.ownerCt, me = grid.RenderUtil;
			if(!me || !me.args){
				return val;
			}
		}
		var arg = me.args.overqty[field];
		if(arg && arg.length > 0){
			var data = record.data,keys = Ext.Object.getKeys(data),formu = arg[0];
			Ext.each(keys, function(k){
				if(contains(formu, k, true)) {
					formu = formu.replace(new RegExp(k,"gm"), '(' + data[k] + ')');
				}
			});
			var d = 0;
			try {
				d = eval(formu);
			} catch (e) {
				d = 0;
			}
			if(d == null || d == '' || String(d) == 'NaN' || String(d).length == 0)
				d = 0;
			var _val = val, _d = d;
			if(column.format) {
				_val = Ext.util.Format.number(val, column.format);
				_d = Ext.util.Format.number(d, column.format);
			}
			val = _val;
			if(_val != _d) {
				//鉴于小数问题 保留2位小数
				if(column.editor || (column.getEditor && column.getEditor())) {
					val = (!Ext.isNumber(val) || val == 0) ? d : val;
					if(Number(val) - d > 0){
						val = d;
						showError('请不要输入超过最大数量' + d + "的值!");
					}
				} else {
					val = d;
				}
				var _v = val;
				if(column.xtype == 'numbercolumn') {
					_v = Ext.util.Format.number(val, column.format);
				}
				if(Ext.util.Format.number(record.data[field], column.format) != _v){
					record.set(field, val);
				}
				return _v; 
			} else {
				return val;
			}
		}
	},
	/**
	 * 公式计算
	 * 通用方法
	 * @expression eval:pd_orderprice/(1+pd_taxrate/100)*pd_inqty 
	 */
	eval: function(val, meta, record, x, y, store, view){
		meta.tdCls = "x-grid-cell-renderer-bl";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		if(!me || !me.args){
			grid = grid.ownerCt, me = grid.RenderUtil;
			if(!me || !me.args){
				return val;
			}
		}
		var arg = me.args.eval[field];
		if(arg && arg.length > 0){
			val = record.get(field);
			var data = record.data,keys = Ext.Object.getKeys(data),formu = arg[0];
			Ext.each(keys, function(k){
				if(contains(formu, k, true)) {
					formu = formu.replace(new RegExp(k,"gm"), '(' + data[k] + ')');
				}
			});
			var d = 0;
			try {
				d = eval(formu);
			} catch (e) {
				d = 0;
			}
			if(d == null || String(d) == '' || String(d) == 'NaN' || String(d).length == 0)
				return val;
			var _val = val, _d = d;
			if(column.format) {
				_val = Ext.util.Format.number(val, column.format);
				_d = Ext.util.Format.number(d, column.format);
			}
			if(_val != _d) {
				if(record.modified == null || record.modified[field] == null) {
					val = d;
					record.set(field, d);
					record.modified[field] = d;
				}
			}
		}
		return val;
	},
	/**
	 * 适用于所有日期类型
	 * 超时提示
	 * 当v < 当前日期时,表示超时 
	 * 需要在dataListDetail表的dld_render 配置 overtime:{dateField}
	 * 注意:日期类型貌似不能直接render，所以要借助其他字段
	 * @expression overtime:pu_delivery
	 */
	overtime: function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		if(!me || !me.args){
			grid = grid.ownerCt, me = grid.RenderUtil;
			if(!me || !me.args){
				return val;
			}
		}
		var d = new Date();
		var arg = me.args.overtime[field];
		if(arg && arg.length > 0){
			d = record.data[arg[0]];//me.args[0]为需要逻辑判断的日期字段
		}
		if(!Ext.isDate(d)){
			d = Ext.Date.parse(d, 'Y-m-d H:i:s') || Ext.Date.parse(d, 'Y-m-d');
		}
		if(d < new Date()){
			return '<img src="' + basePath + 'resource/images/renderer/important.png" title="时间结束:' + Ext.Date.toString(d) + '">' +
			'<span style="color:blue;padding-left:2px" title="时间结束:' + Ext.Date.toString(d) + '">' + val + '</span>';
		} else {
			return val;
		}
	},

	pd_auditstatus_show:function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,column = grid.columns[y],field = column.dataIndex;
		var returnStr;
		if(val == 'PARTAR'||val == '部分开票'){
			returnStr = '部分开票';
		}else if(val == 'TURNAR'||val == '已开票'){
			returnStr = '已开票';
		}else{
			returnStr = '未开票';
		}
		if(val != returnStr) {
			val = returnStr;
			record.set(field, val);
		}
		return val;
	},
	
	defaultValue:function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		if(val == null || val == '' || val == 0){
			if(!me || !me.args){
				grid = grid.ownerCt, me = grid.RenderUtil;
				if(!me || !me.args){
					return val;
				}
			}
			var v = record.data[field];
			if(v == null || v == '' || (column.xtype == 'numbercolumn' && v == 0)){
				var arg = me.args.defaultValue[field];
				if(arg && arg.length > 0){
					if(arg=='em_name'){
						val=em_name;
					}else{
						val = arg[0];
					}
				}
				if(val != null && val != '' && val != 0){
					record.set(field, val);
				}
			}
		}
		if(column.xtype == 'numbercolumn' && val != 0){
			val = Ext.util.Format.number(val, column.format);
		}
		return val; 
	},
	/**
	 * 解析链接，并打开
	 * @expression jsps/scm/sale/sendnotify.jsp?formConditionISsn_id={snd_snid}&gridConditionISsnd_snid={snd_snid}
	 */
	link: function(val, m, record, x, y, store, view) {
		var grid = view.ownerCt, me = grid.RenderUtil,column = grid.columns[y], url = column.logic|| (me && me.args ? me.args.link[column.dataIndex][0] : '');
		if(typeof(me)=='undefined'){
			 me = grid.ownerCt.RenderUtil;
		}
		if(url) {
			var res = '';
			if(url == 'necessaryField' || url == 'orNecessField') {
				if(!val)
					res = '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">';
				url = (me.args && me.args.link[column.dataIndex]) ? me.args.link[column.dataIndex][0] : '';
			}else if(url == 'ignore'){
				url = (me.args && me.args.link[column.dataIndex]) ? me.args.link[column.dataIndex][0] : '';
			}
			var index = 0, length = url.length, s, e;
			while(index < length) {
				if((s = url.indexOf('{', index)) != -1 && (e = url.indexOf('}', s + 1)) != -1) {
					url = url.substring(0, s) + record.get(url.substring(s+1, e)) + url.substring(e+1);
					index = e + 1;
				} else {
					break;
				}
			}
			return res + '<a href="javascript:openUrl(\'' + url + '\');">' + val + '</a>';
		}
		return val;
	},
	/**
	 * 应(收)付发票、其它应收（付）单链接
	 * @param {} val
	 * @param {} m
	 * @param {} record
	 * @param {} x
	 * @param {} y
	 * @param {} store
	 * @param {} view
	 * @return {}
	 */
	bill_link: function(val, m, record, x, y, store, view) {
		var grid = view.ownerCt, me = grid.RenderUtil,column = grid.columns[y], url = '',kind = column.logic||(me && me.args ? me.args.bill_link[column.dataIndex][0] : '');
		if(typeof(me)=='undefined'){
			 me = grid.ownerCt.RenderUtil;
		}
		if(kind){
			if(kind == 'necessaryField' || kind == 'orNecessField') {
				if(!val)
					return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">';
				kind = me.args ? me.args.bill_link[column.dataIndex][0] : '';
			}else if(kind == 'ignore'){
				kind = me.args ? me.args.bill_link[column.dataIndex][0] : '';
			}
			var type = record.data[kind];
			if(!type) {
				Ext.Ajax.request({
					url: basePath + 'fa/ars/getOrderType.action',
					async: false,
					params: {
						caller: caller,
						id : record.data[grid.keyField],
						code: val
					},
					callback: function(opt, s, res) {
						var r = Ext.decode(res.responseText);
						if(r.success) {
							type = r.data;
							record.set(kind,type);
						}
					}
				});
			}
			if(type){
				switch(type){
					case '应收发票' : url = 'jsps/fa/ars/arbill.jsp?whoami=ARBill!IRMA'; break;
					case '其它应收单' : url = 'jsps/fa/ars/arbill.jsp?whoami=ARBill!OTRS'; break;
					case '应付发票' : url = 'jsps/fa/ars/apbill.jsp?whoami=APBill!CWIM'; break;
					case '其它应付单' : url = 'jsps/fa/ars/apbill.jsp?whoami=APBill!OTDW'; break;
					case '初始化' : 
						if(caller=='PayBalance'){
							url = 'jsps/fa/ars/apbill.jsp?whoami=APBill!CWIM';
						}else{
							url = 'jsps/fa/ars/arbill.jsp?whoami=ARBill!IRMA';
						}
						break;
				}
				url += '&formCondition=ab_codeIS'+val+'&gridCondition=abd_codeIS'+val
				return '<a href="javascript:openUrl(\'' + url + '\');">' + val + '</a>';
			}
		}
		
		return val;
	},
	/**
	 * val不能小于当前日期
	 * @expression undertime
	 */
	undertime: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var d = Ext.Date.parse(Ext.Date.toString(new Date()), 'Y-m-d');
		var grid = view.ownerCt,column = grid.columns[y],field = column.dataIndex;
		val = record.data[field];
		if(val == null) {
			val = Ext.Date.toString(d);
		} else {
			if(!Ext.isDate(val)){
				val = Ext.Date.parse(val, 'Y-m-d');
			}
			if(val < d){
				val = Ext.Date.toString(d);
				showError("日期不能小于当前日期!");
			} else {
				val = Ext.Date.toString(new Date(val));
			}
		}
		if(Ext.isDate(record.data[field])){
			if(Ext.Date.toString(record.data[field]) != val){
				record.set(field, val);
			}
		} else {
			if(record.data[field] != val){
				record.set(field, val);
			}
		}
		return val;
	},
	/**
	 * 适用于所有数字类型
	 * 数量过大提示
	 * 当val > d时,表示过大 
	 * 需要在dataListDetail表的dld_render 配置 oversize:{num}
	 * @expression oversize:100
	 * @expression oversize:pd_qty
	 */
	oversize: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		if(!me || !me.args){
			grid = grid.ownerCt, me = grid.RenderUtil;
			if(!me || !me.args){
				return val;
			}
		}
		var d = val;
		var arg = me.args.oversize[field];
		if(arg && arg.length > 0){
			if(Ext.isNumber(arg[0])){
				d = Number(arg[0]);
			} else {
				d = Number(record.data[arg[0]]);
			}
		}
		if(val > d){
			return '<img src="' + basePath + 'resource/images/renderer/important.png" title="数值过大:' + val + '>' + d + '">' + 
			'<span style="color:blue;padding-left:2px" title="数值过大:' + val + '>' + d + '">' + val + '</span>';
		} else {
			return val;
		}
	},
	/**
	 * 适用于所有数字类型
	 * 数量过小提示
	 * 当val < d时,表示过小 
	 * 需要在dataListDetail表的dld_render 配置 undersize:{num}
	 * @param arg为number类型或某字段
	 * @expression undersize:100
	 * @expression undersize:pd_yqty
	 */
	undersize: function(val, meta, record, x, y, store, view){
		meta.style = "background:#CDB5CD;";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex,uCol = null;
		if(!me || !me.args){
			grid = grid.ownerCt, me = grid.RenderUtil;
			if(!me || !me.args){
				return val;
			}
		}
		var d = val;
		var arg = me.args.undersize[field];
		if(arg && arg.length > 0){
			if(!Ext.isNumber(Number(arg[0]))){
				d = Number(record.data[arg[0]]);
				var rr = grid.columns.filter(function(c){
					return c.dataIndex == arg[0];
				});
				if(rr.length > 0) {
					uCol = rr[0];
				}
			} else {
				d = Number(arg[0]);
			}
		}
		if(record.data[field] != val){
			record.set(field, val);
		}
		if(!Ext.isNumber(val)){
			val = d;
			record.set(field, val);
			showError('请输入数字!');
		} else{
			if(val < d){
				if(val != 0) {
					var err = '请不要输入低于';
					if(uCol != null) {
						err += '<' + uCol.text + '>';
					}
					showError(err + '<' + d + ">的值!");
				}
				val = d;
				record.set(field, val);
			}
		}
		var str = val;
		if(column.format) {
			str = Ext.util.Format.number(val, column.format);
		}
		if(val < d){
			return '<img src="' + basePath + 'resource/images/renderer/important.png" title="数值过小:' + val + '<' + d + '">' + 
			'<span style="color:blue;padding-left:2px" title="数值过小:' + val + '<' + d + '">' + str + '</span>';
		} else {
			return str;
		}
	},
	/**
	 * 适用于所有数字类型
	 * 数量介于{min}~{max}
	 * @expression betweensize:0:100
	 * @expression betweensize:0:pd_qty
	 * @expression betweensize:pd_tqty:pd_qty
	 */
	betweensize: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		if(!me || !me.args){
			grid = grid.ownerCt, me = grid.RenderUtil;
			if(!me || !me.args){
				return val;
			}
		}
		var arg = me.args.betweensize[field];
		var min = 0;
		var max = 0;
		if(arg && arg.length > 0){
			if(!Ext.isNumber(Number(arg[0]))){
				min = record.modified[arg[0]];
			} else {
				min = Number(arg[0]);
			}
			if(!Ext.isNumber(Number(arg[1]))){
				max = record.modified[arg[1]];
			} else {
				max = Number(arg[1]);
			}
		}
		if(record.data[field] != val){
			record.set(field, val);
		}
		if(!Ext.isNumber(val)){
			val = max;
			record.set(field, val);
			showError('请输入数字!');
		} else{
			if(val < min){
				val = max;
				record.set(field, val);
				showError('请不要输入低于' + min + "的值!");
			} else if(val > max){
				val = max;
				record.set(field, val);
				showError('请不要输入大于' + max + "的值!");
			}
		}
		if(val < min){
			return '<img src="' + basePath + 'resource/images/renderer/important.png" title="数值过小:' + val + '<' + min + '">' + 
			'<span style="color:blue;padding-left:2px" title="数值过小:' + val + '<' + min + '">' + val + '</span>';
		} else if(val > max){
			return '<img src="' + basePath + 'resource/images/renderer/important.png" title="数值过大:' + val + '<' + max + '">' + 
			'<span style="color:blue;padding-left:2px" title="数值过大:' + val + '<' + max + '">' + val + '</span>';
		} else {
			return val;
		}
	},
	/**
	 * 通用方法
	 * 值是否为空
	 * @expression isnull
	 */
	isnull: function(val, meta, record){
		meta.style = "background:#C6E2FF;";
		if(val == null || val == ''){
			return '<img src="' + basePath + 'resource/images/renderer/important.png" title="无数据">' + 
			'<span style="color:blue;padding-left:2px" title="无数据">' + val + '</span>';
		} else {
			return val;
		}
	},
	/**
	 * 通用方法
	 * 值=arg[0]+arg[1]+...
	 * @expression plus:pd_tqty:pd_yqty
	 * @expression plus:pd_tqty:100:pd_yqty
	 */
	plus: function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex,format = column.format,type = column.xtype;
		if(!me || !me.args){
			grid = grid.ownerCt, me = grid.RenderUtil;
			if(!me || !me.args){
				return val;
			}
		}
		var d = 0;
		var arg = me.args.plus[field];
		if(arg && arg.length > 0){
			Ext.each(arg, function(a, index){
				if(Ext.isNumber(a)){
					d += Number(a);
				} else {
					d += Number(record.data[a]);
				}
			});
		}
		if(record.data[field] != d){
			record.set(field, d);
		}
		if(format && type =='numbercolumn'){
			d = Ext.util.Format.number(d, format);
		}
		return '<img src="' + basePath + 'resource/images/renderer/finishrecord.png">' + 
		'<span style="color:blue;padding-left:2px">' + d + '</span>';
	},
	/**
	 * 通用乘法方法
	 * 值=arg[0]*arg[1]*...
	 * @expression multiply:pd_tqty:pd_price
	 * @expression multiply:pd_tqty:100:pd_rate
	 */
	multiply: function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt, me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex,format = column.format,type = column.xtype;
		if(!me || !me.args){
			grid = grid.ownerCt, me = grid.RenderUtil;
			if(!me || !me.args){
				return val;
			}
		}
		var d = 1;
		var red= d;
		var arg = me.args.multiply[field];
		if(arg && arg.length > 0){
			Ext.each(arg, function(a, index){
				if(Ext.isNumber(a)){
					d = d * Number(a);
				} else {
					d = d * Number(record.data[a]);
				}
				if(format&&type =='numbercolumn'){
					red = Ext.util.Format.number(d,format);
				}else{
					red = d;
				}
			});
		}
		if(record.data[field] != d){
			record.set(field, d);
		}
		return '<img src="' + basePath + 'resource/images/renderer/finishrecord.png">' + 
		'<span style="color:blue;padding-left:2px">' + red + '</span>';
	},
	/**
	 * 通用方法
	 * A floating:B
	 * 比较A相对于B的浮动变化
	 * @expression floating:B
	 */
	floating: function(val, meta, record, x, y, store, view) {
		
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		if(!me || !me.args){
			return val;
		}
		var arg = me.args.floating[field], a = null, b = null, f = null, n = null, istxt = true;
		if(column.xtype == 'datecolumn') {
			if(!val) return null;
			a = record.get(arg);
			b = val; 
			f = (column.format || 'Y-m-d');
			n = Ext.Date.format(val, f);
			istxt = false;
		} else if(column.xtype == 'numbercolumn') {
			a = Number(record.get(arg));
			b = Number(val); 
			f = (column.format || '0,000');
			n = Ext.util.Format.number(val, f);
			istxt = false;
		} else {
			a = record.get(arg);
			b = val; 
		}
		if(istxt) {
			if(a != b) {
				return '<span style="color:red;padding-left:2px">' + val + '</span>';
			} else {
				return val;
			}
		} else {
			if(a < b) {
				return '<img src="' + basePath + 'resource/images/16/up.png">' + 
				'<span style="color:red;padding-left:2px">' + n + '</span>';
			} else if(a > b) {
				return '<img src="' + basePath + 'resource/images/16/down.png">' + 
				'<span style="color:red;padding-left:2px">' + n + '</span>';
			} else if(a == 0 && b == 0) {
				return '';
			}
		}
		return n;
	},
	/**
	 * 非通用方法(批量报价专用)
	 * A floating:B
	 * 比较A相对于B的浮动变化
	 * @expression floating:B
	 */
	anotherfloating: function(val, meta, record, x, y, store, view) {
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		if(!me || !me.args){
			return val;
		}
		var arg = me.args.anotherfloating[field], a = null, b = null, f = null, n = null, istxt = true;
		if(column.xtype == 'datecolumn') {
			if(!val) return null;
			a = record.get(arg);
			b = val; 
			f = (column.format || 'Y-m-d');
			n = Ext.Date.format(val, f);
			istxt = false;
		} else if(column.xtype == 'numbercolumn') {
			a = Number(record.get(arg));
			b = Number(val); 
			f = (column.format || '0,000');
			n = Ext.util.Format.number(val, f);
			istxt = false;
		} else {
			a = record.get(arg);
			b = val; 
		}
		//if(istxt) {
		var strRed = '<span style="color:red;padding-left:2px">'+ n +'</span>';	
		var strGreen = '<span style="color:green;padding-left:2px">'+ n +'</span>';
			if(a < b) {
				return  strRed;
			} else if(a > b) {
				return strGreen;
			} else if(a == 0 && b == 0) {
				return '';
			}
			return n;
	},
	/**
	 * 非通用方法(BOM核价单专用)
	 * A floating:B
	 * 比较A相对于B的浮动变化 A为0时不进行比较
	 * @expression floating:B
	 * */
	floatingexcept0: function(val, meta, record, x, y, store, view) {
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		if(!me || !me.args){
			return val;
		}
		var arg = me.args.floatingexcept0[field], a = null, b = null, f = null, n = null, istxt = true;
		if(column.xtype == 'datecolumn') {
			if(!val) return null;
			a = record.get(arg);
			b = val; 
			f = (column.format || 'Y-m-d');
			n = Ext.Date.format(val, f);
			istxt = false;
		} else if(column.xtype == 'numbercolumn') {
			a = Number(record.get(arg));
			b = Number(val); 
			f = (column.format || '0,000');
			n = Ext.util.Format.number(val, f);
			istxt = false;
		} else {
			a = record.get(arg);
			b = val; 
		}
		
		if(istxt) {
			if(a != b) {
				return '<span style="color:red;padding-left:2px">' + val + '</span>';
			} else {
				return val;
			}
		} else {
			if(b == 0){
				return n;
			}else if(a > b) {
				return '<img src="' + basePath + 'resource/images/16/greendown.png">' + 
				'<span style="color:red;padding-left:2px">' + n + '</span>';
			}else{
				return n;
			}
		}
		return n;
	},
	ars_prodtoapbill_outqty:function(val, meta, record, x, y){
		var inqty = 0;
		var outqty = 0;
		var returnqty = 0;
		if(record.data['pd_inqty']!=null&&record.data['pd_inqty']){
			inqty = Ext.Number.from(record.data['pd_inqty'],0);
		}
		if(record.data['pd_outqty']!=null&&record.data['pd_outqty']){
			inqty = Ext.Number.from(record.data['pd_inqty'],0);
		}
		returnqty=outqty-inqty;

		if(val != returnqty){
			record.set('pd_outqty', returnqty);
			val = returnqty;
		}
		return val;
	},
	ars_prodtoarbill_price:function(val, meta, record, x, y){
		var orderprice = 0.00;
		var sendprice = 0.00;
		var returnprice = 0.00;
		if(record.data['pd_orderprice']!=null&&record.data['pd_orderprice']){
			orderprice = Ext.Number.from(record.data['pd_orderprice'],0.00);
		}
		if(record.data['pd_sendprice']!=null&&record.data['pd_sendprice']){
			sendprice = Ext.Number.from(record.data['pd_sendprice'],0.00);
		}
		returnprice=Math.abs(orderprice-sendprice);
		returnprice=Ext.util.Format.number(returnprice,'0.00');
		if(val != returnprice){
			record.set('pd_showprice', returnprice);
			val = returnprice;
		}
		return val;
	},
	/**
	 * 出货数量   outqty - inqty  
	 * @param val
	 * @param meta
	 * @param record
	 * @param x
	 * @param y
	 * @returns
	 */
	ars_prodtoapbill_outqty:function(val, meta, record, x, y){
		var inqty = 0;
		var outqty = 0;
		var returnqty = 0;
		if(record.data['pd_inqty']!=null&&record.data['pd_inqty']){
			inqty = Ext.Number.from(record.data['pd_inqty'],0);
		}
		if(record.data['pd_outqty']!=null&&record.data['pd_outqty']){
			outqty = Ext.Number.from(record.data['pd_outqty'],0);
		}
		returnqty=outqty-inqty;

		if(val != returnqty){
			record.set('pd_showqty', returnqty);
			val = returnqty;
		}
		return val;
	},
	/**
	 * @CRM
	 * 客户关系商机状态显示
	 */
	chanceAllstatus: function(val, meta, record,x,y){	
		var code=record.data['cd_chancecode'];
		var str=val.replace(/1/g,'√');
		str=str.replace(/0/g,'O'); 
		return '<a href="javascript:showchancestatus(' +  '\''+code+'\'' +');">' + str + '</a>';

	},
	/**
	 * @CRM
	 * 客户关系商机状态显示
	 */
	chancestatus: function(val, meta, record,x,y){
		var str='';
		if(val=='0'){
			str='O';
		}
		if(val=='1'){
			str='√';
		}	   
		return str;
	},
	/**
	 * @CRM
	 * 客户关系商机状态显示
	 */
	inquiryturnstatus: function(val, meta, record,x,y){
		var str='';
		if(val=='0'){
			str='否';
		}
		if(val=='1'){
			str='未选择';
		}
		if(val=='-1'){
			str='是';
		}	   
		return str;
	},
	oameeting:function(val,meta, record){
		if(val=='0'){
			return ' ';
		}
		var url='jsps/oa/meeting/meetingroomapply.jsp';
		var title='会议室申请单';
		return '<a href="javascript:openGridUrl(\''+val+'\''+',\'ma_id\',\'mad_maid\',\''+url+'\',\''+title+'\''+ ');">' + '√' + '</a>';
	},
	/**
	 * 验收数量   inqty - outqty  
	 * @param val
	 * @param meta
	 * @param record
	 * @param x
	 * @param y
	 * @returns
	 */
	ars_prodtoapbill_inqty:function(val, meta, record, x, y){
		var inqty = 0;
		var outqty = 0;
		var returnqty = 0;
		if(record.data['pd_inqty']!=null&&record.data['pd_inqty']){
			inqty = Ext.Number.from(record.data['pd_inqty'],0);
		}
		if(record.data['pd_outqty']!=null&&record.data['pd_outqty']){
			outqty = Ext.Number.from(record.data['pd_outqty'],0);
		}
		returnqty=inqty-outqty;

		if(val != returnqty){
			record.set('pd_showqty', returnqty);
			val = returnqty;
		}
		return val;
	},
	/**
	 * 验收数量的绝对值  pd_inqty -已经转发票数量  pd_showinvoqty   = 本次可转发票数量 pd_thisvoqty
	 * @param val
	 * @param meta
	 * @param record
	 * @param x
	 * @param y
	 * @returns
	 */
	ars_prodtoapbill_thisqty:function(val, meta, record, x, y){
		meta.style = "background:#C6E2FF;";
		//pd_thisvoqty = pd_inqty-pd_showinvoqty        inqty要绝对值
		var thisvoqty = 0;
		var inqty = 0;
		var invoqty = 0;
		if(record.data['pd_showqty']!=null&&record.data['pd_showqty']!=''){
			inqty = Math.abs(Ext.Number.from(record.data['pd_showqty'],0));
		}
		if(record.data['pd_showinvoqty']!=null&&record.data['pd_showinvoqty']){
			invoqty = Ext.Number.from(record.data['pd_showinvoqty'],0);
		}

		thisvoqty=inqty-invoqty;
		if(val != thisvoqty){
			record.set('pd_thisvoqty', thisvoqty);
			val = thisvoqty;
		}
		return val;
	},
	
	/**
	 * 成本表链接到制造单
	 * 
	 **/
	MakeCostMakeHref:function(val,meta,record){
		var url='jsps/pm/make/makeBase.jsp?whoami=';
		var ma_id=record.data.ma_id, ma_tasktype = record.data.cd_maketype, type;
		if(ma_tasktype == 'OS'){
			type = 'Make';
		} else if(ma_tasktype == 'MAKE'){
			type = 'Make!Base';
		}
		url = url + type + '&formCondition=ma_idIS'+ma_id+'&gridCondition=mm_maidIS'+ma_id;
		return '<a href="javascript:openUrl(\''+url+'\');">' + val + '</a>';
	},

	/**
	 * 出入库单号链接
	 * 
	 **/
	ProdioHref:function(val,meta,record){
		var url='jsps/scm/reserve/prodInOut.jsp?whoami=';
		var caller;
		var piclass=record.data.pi_class, piid = record.data.pi_id;
		if(piclass == '采购验收单'){
			caller = 'ProdInOut!PurcCheckin';
		} else if(piclass == '采购验退单'){
			caller = 'ProdInOut!PurcCheckout';
		} else if(piclass == '其它采购入库单'){
			caller = 'ProdInOut!OtherPurcIn';
		} else if(piclass == '完工入库单'){
			caller = 'ProdInOut!Make!In';
		} else if(piclass == '其它采购出库单'){
			caller = 'ProdInOut!OtherPurcOut';
		} else if(piclass == '换货出库单'){
			caller = 'ProdInOut!ExchangeOut';
		} else if(piclass == '换货入库单'){
			caller = 'ProdInOut!ExchangeIn';
		} else if(piclass == '出货单'){
			caller = 'ProdInOut!Sale';
		} else if(piclass == '委外领料单'){
			caller = 'ProdInOut!OutsidePicking';
		} else if(piclass == '研发退料单'){
			caller = 'ProdInOut!YFIN';
		} else if(piclass == '研发领料单'){
			caller = 'ProdInOut!YFOUT';
		} else if(piclass == '辅料入库单'){
			caller = 'ProdInOut!FLIN';
		} else if(piclass == '辅料出库单'){
			caller = 'ProdInOut!FLOUT';
		} else if(piclass == '借货出货单'){
			caller = 'ProdInOut!SaleBorrow';
		} else if(piclass == '借货归还单'){
			caller = 'ProdInOut!OutReturn';
		} else if(piclass == '委外补料单'){
			caller = 'ProdInOut!OSMake!Give';
		} else if(piclass == '不良品入库单'){
			caller = 'ProdInOut!DefectIn';
		} else if(piclass == '不良品出库单'){
			caller = 'ProdInOut!DefectOut';
		} else if(piclass == '库存初始化'){
			caller = 'ProdInOut!ReserveInitialize';
		} else if(piclass == '报废单'){
			caller = 'ProdInOut!StockScrap';
		} else if(piclass == '盘亏调整单'){
			caller = 'ProdInOut!StockLoss';
		} else if(piclass == '盘盈调整单'){
			caller = 'ProdInOut!StockProfit';
		} else if(piclass == '拆件入库单'){
			caller = 'ProdInOut!PartitionStockIn';
		} else if(piclass == '其它入库单'){
			caller = 'ProdInOut!OtherIn';
		} else if(piclass == '生产领料单'){
			caller = 'ProdInOut!Picking';
		} else if(piclass == '生产退料单'){
			caller = 'ProdInOut!Make!Return';
		} else if(piclass == '销售退货单'){
			caller = 'ProdInOut!SaleReturn';
		} else if(piclass == '委外验收单'){
			caller = 'ProdInOut!OutsideCheckIn';
		} else if(piclass == '委外验退单'){
			caller = 'ProdInOut!OutesideCheckReturn';
		} else if(piclass == '委外退料单'){
			caller = 'ProdInOut!OutsideReturn';
		} else if(piclass == '拨出单'){
			caller = 'ProdInOut!AppropriationOut';
		} else if(piclass == '拨入单'){
			caller = 'ProdInOut!AppropriationIn';
		} else if(piclass == '销售拨出单'){
			caller = 'ProdInOut!SaleAppropriationOut';
		} else if(piclass == '销售拨入单'){
			caller = 'ProdInOut!SalePutIn';
		} else if(piclass == '其它出库单'){
			caller = 'ProdInOut!OtherOut';
		} else if(piclass == '生产补料单'){
			caller = 'ProdInOut!Make!Give';
		} else if(piclass == '用品领用单'){
			caller = 'ProdInOut!GoodsPicking';
		} else if(piclass == '用品验收单'){
			caller = 'ProdInOut!GoodsIn';
		} else if(piclass == '用品验退单'){
			caller = 'ProdInOut!GoodsOut';
		} else if(piclass == '用品退仓单'){
			caller = 'ProdInOut!GoodsShutout';
		} else if(piclass == '用品借用单'){
			caller = 'ProdInOut!GoodsLend';
		} else if(piclass == '用品归还单'){
			caller = 'ProdInOut!GoodsReturn';
		} else if(piclass == '成本调整单'){
			caller = 'ProdInOut!CostChange';
		} else if(piclass == '工模领料单'){
			caller = 'ProdInOut!ModelOut';
		} else if(piclass == '工模退料单'){
			caller = 'ProdInOut!ModelIn';
		} else if(piclass == '设备领料单'){
			caller = 'ProdInOut!EquipmentOut';
		} else if(piclass == '设备退料单'){
			caller = 'ProdInOut!EquipmentIn';
		} else if(piclass == '耗材领料单'){
			caller = 'ProdInOut!SuppliesOut';
		} else if(piclass == '耗材退料单'){
			caller = 'ProdInOut!SuppliesIn';
		} else if(piclass == '办公用品领料单'){
			caller = 'ProdInOut!WorkOut';
		} else if(piclass == '办公用品退料单'){
			caller = 'ProdInOut!WorkIn';
		} 
		url = url + caller + '&formCondition=pi_idIS'+piid+'&gridCondition=pd_piidIS'+piid;
		return '<a href="javascript:openUrl(\''+url+'\');">' + val + '</a>';
	},
	
	/**
	 * 采购单号链接
	 * 
	 **/
	PuWithOAHref:function(val,meta,record){
		var url=null;
		var caller;
		var piclass=record.data.ppdd_type, pucode = record.data.ppdd_pucode;
		if(piclass == '采购单'){
			url='jsps/scm/purchase/purchase.jsp?formCondition=pu_codeIS'+pucode+'&gridCondition=pd_codeIS'+pucode;
		} else if(piclass == '模具采购单'){
			url='jsps/pm/mould/purcMould.jsp?whoami=Purc!Mould&formCondition=pm_codeIS'+pucode+'&gridCondition=pmd_codeIS'+pucode;
		} else if(piclass == '用品采购单'){
			url='jsps/oa/appliance/oapurchase.jsp?formCondition=op_codeIS'+pucode+'&gridCondition=od_codeIS'+pucode;
		} else if(piclass == '印章申请单'){
			url='jsps/oa/fee/feePleaseYZSYSQ.jsp?whoami=FeePlease!YZSYSQ&formCondition=fp_codeIS'+pucode+'&gridCondition=fpd_codeIS'+pucode;
		}
		if(url != null){
			return '<a href="javascript:openUrl(\''+url+'\');">' + val + '</a>';
		}
		return val;
	},

	/**
	 * 应收发票维护 计算不含税金额   不含税金额，需要改为“本次开票不含税金额”=本次开票价税合计 / (1+税率)
	 * 
	 * 
	 * 
	 */
	ars_arbill_tax: function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y];
		var tax = Number(record.data['abd_qty'])*Number(record.data['abd_thisvoprice'])/(1+Number(record.data['abd_taxrate'])/100);
		if(tax != null && tax >= 0) {
			record.set('abd_noaramount', tax);
			val = tax;
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		return val;
	},
	/**
	 * 应收发票维护 计算不含税金额   不含税金额，需要改为“本次开票不含税金额”=本次开票价税合计 / (1+税率)
	 * 
	 * 
	 * 
	 */
	arp_apbill_tax: function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y];
		var tax = Number(record.data['abd_apamount'])/(1+Number(record.data['abd_taxrate'])/100);
		if(tax != null && tax >= 0) {
			record.set('abd_noapamount', tax);
			val = tax;
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		return val;
	},

	/**
	 * 应收发票维护 计算不含税金额   不含税金额，需要改为“本次开票不含税金额”=本次开票价税合计 / (1+税率)
	 * 
	 * 
	 * 
	 */
	arbill_show_invototal: function(val, meta, record, x, y, store, view){
		var sourcekind = record.data['abd_sourcekind'];
		var grid = view.ownerCt,column = grid.columns[y];
		var pd_invototal = Number(record.data['pd_invototal']);            //出入库单转出
		var gsd_invototal = Number(record.data['gsd_invototal']);				  //发出商品数据
		var abd_totalbillprice = Number(record.data['abd_totalbillprice']);//初始化数据
		//转发类型是发出商品
		if(sourcekind == 'GOODSSEND'){
			if(val!=gsd_invototal){
				record.set('abd_totalbillprice', gsd_invototal);
				gsd_invototal=val;
			}
			return Ext.util.Format.number(gsd_invototal, column.format); 
			//出入库单转出
		}else if(sourcekind == 'PRODIODETAIL'){
			if(val!=pd_invototal){
				record.set('abd_totalbillprice', pd_invototal);
				pd_invototal=val;
			}
			return Ext.util.Format.number(pd_invototal, column.format); 

			//初始化
		}else if(sourcekind == 'INITIALIZATION'){

			if(val!=abd_totalbillprice){
				record.set('abd_totalbillprice', abd_totalbillprice);
				abd_totalbillprice=val;
			}
			return Ext.util.Format.number(abd_totalbillprice, column.format); 
		}

	},
	/**
	 * 应收发票维护 计算不含税金额   不含税金额，需要改为“本次开票不含税金额”=本次开票价税合计 / (1+税率)
	 * 
	 * 
	 * 
	 */
	apbill_show_invototal: function(val, meta, record, x, y, store, view){
		var sourcekind = record.data['abd_sourcekind'];
		var grid = view.ownerCt,column = grid.columns[y];
		var pd_invototal = Number(record.data['pd_invototal']);            //出入库单转出
		var esd_invototal = Number(record.data['esd_invototal']);				  //发出商品数据
		var abd_totalbillprice = Number(record.data['abd_totalbillprice']);//初始化数据
		//转发类型是发出商品
		if(sourcekind == 'ESTIMATE'){
			if(val!=esd_invototal){
				record.set('abd_totalbillprice', esd_invototal);
				esd_invototal=val;
			}

			return Ext.util.Format.number(esd_invototal,column.format); 
			//出入库单转出
		}else if(sourcekind == 'PRODIODETAIL'){
			if(val!=pd_invototal){
				record.set('abd_totalbillprice', pd_invototal);
				pd_invototal=val;
			}
			return Ext.util.Format.number(pd_invototal,column.format); 

			//初始化
		}else if(sourcekind == 'INITIALIZATION'){
			if(val!=abd_totalbillprice){
				record.set('abd_totalbillprice', abd_totalbillprice);
				abd_totalbillprice=val;
			}
			return Ext.util.Format.number(abd_totalbillprice, column.format); 
		}

	},

	/**
	 * 应付发票维护 计算不含税金额   不含税金额，需要改为“本次开票不含税金额”=本次开票价税合计 / (1+税率)
	 * 
	 * 
	 * 
	 */
	ars_apbill_tax: function(val, meta, record, x, y){
		var tax = Number(record.data['abd_apamount'])/(1+Number(record.data['abd_taxrate'])/100);
		var  tval=Ext.util.Format.number(tax,'0.00');
		if(val != tval){
			record.set('abd_noapamount', tval);
			val = tval;
		}
		return tval;
	},

	colspan: function(val, meta, record, x, y, store, view){
		meta.tdAttr = "rowspan=8;";
	},
	nullcolspan: function(val, meta){
		meta.tdAttr = "rowspan=0;";
	},
	CRMHref:function(val, meta, record,x,y){
		var me = this.RenderUtil || this;
		var field = this.columns[y].dataIndex;
		var KeyValue = 0;
		var url='';
		var title='';
		var arg = me.args.CRMHref[field];
		var keyField=arg[0];
		var data=record.data;
		if(arg && arg.length > 0){
			KeyValue=data[field];
			url=arg[1];
			title=arg[2];
		}
		if(keyField.indexOf('code')>=0){
			url+='?mr_taskcode='+KeyValue;
			Ext.Ajax.request({
				url: basePath+'crm/getReportCode.action',
				async:false,
				params:{
					mt_code:KeyValue
				},
				method:'post',
				callback:function(options,success,response){
					var res = new Ext.decode(response.responseText);
					url+='&whoami='+res.mt_reportcode;
				}
			});
		}else{
			url+='?mr_id='+KeyValue;
			url+='&formCondition=mr_idIS'+KeyValue+'&gridCondition=mrd_mridIS'+KeyValue;
			Ext.Ajax.request({
				url: basePath+'crm/getReportCodeById.action',
				async:false,
				params:{
					mr_id:KeyValue
				},
				method:'post',
				callback:function(options,success,response){
					var res = new Ext.decode(response.responseText);
					url+='&whoami='+res.mr_reportcode;
				}
			});
		}
		if(val==''||val==$I18N.common.grid.emptyText) return val;
		if(keyField.indexOf('code')>=0){
			return '<a href="javascript:openUrl(\''+url+'\');">' + val + '</a>';
		}
		return '<a href="javascript:openFormUrl(' + KeyValue + ',\''+keyField+'\',\''+url+'\',\''+title+'\''+ ');">' + val + '</a>';
	},
	CRMtaskHref:function(val, meta, record,x,y){
		var me = this.RenderUtil || this;
		var rt_foid=record.data['rt_foid'];
		var url='jsps/crm/marketmgr/marketresearch/multiform.jsp?'+
		'formCondition=fo_idIS'+rt_foid+'&gridCondition=fd_foidIS'+rt_foid+'&whoami='+record.data['rt_code']+'&cond=rt_idIS'+record.data['rt_id']+'&type=crm';
		var keyField='rt_code';
		var KeyValue=record.data['rt_code']
		var title='调研模板';
		return '<a href="javascript:openUrl2(\''+url+'\',\''+title+'\',\''+keyField+'\',\''+KeyValue+'\');">' + KeyValue + '</a>';
	},
	PXtaskHref:function(val, meta, record,x,y){
		var me = this.RenderUtil || this;
		var rt_foid=record.data['px_foid'];
		var url='jsps/crm/marketmgr/marketresearch/multiform.jsp?'+
		'formCondition=fo_idIS'+rt_foid+'&gridCondition=fd_foidIS'+rt_foid+'&whoami='+record.data['px_code']+'&cond=px_idIS'+record.data['px_id']+'&type=ProductTrain';
		var keyField='px_code';
		var KeyValue=record.data['px_code']
		var title='考核模板';
		return '<a href="javascript:openUrl2(\''+url+'\',\''+title+'\',\''+keyField+'\',\''+KeyValue+'\');">' + KeyValue + '</a>';
	},
	CRMtaskReport:function(val, meta, record,x,y){
		var me = this.RenderUtil || this;
		var reporttemplatecode=record.data['reporttemplatecode'];
		var url='jsps/crm/marketmgr/marketresearch/taskReport.jsp?'+
		'whoami='+reporttemplatecode+'&cond=idIS'
		+record.data['id'];
		var keyField='taskcode';
		var KeyValue=record.data['taskcode']
		var title='调研报告';
		return '<a href="javascript:openUrl2(\''+url+'\',\''+title+'\',\''+keyField+'\',\''+KeyValue+'\');">' + KeyValue + '</a>';
	},
	CRMResearchReport:function(val, meta, record,x,y){
		var me = this.RenderUtil || this;
		var reporttemplatecode=record.data['manuallyscheduled'];
		var url='jsps/crm/marketmgr/marketresearch/researchReport.jsp?'+
		'whoami='+reporttemplatecode+'&cond=idIS'
		+record.data['id'];
		if(record.data['mr_id']!=0){
			url=url+'&formCondition=mr_idIS'+record.data['mr_id']+'&gridCondition=mrd_mridIS'+record.data['mr_id'];
		}
		var keyField='taskcode';
		var KeyValue=record.data['taskcode']
		var title='调研报告';
		return '<a href="javascript:openUrl2(\''+url+'\',\''+title+'\',\''+keyField+'\',\''+KeyValue+'\');">' + KeyValue + '</a>';
	},
	CRMTrainReport:function(val, meta, record,x,y){
		var me = this.RenderUtil || this;
		var reporttemplatecode=record.data['to_tpcode'];
		var url="jsps/crm/marketmgr/resourcemgr/trainReport.jsp?whoami="
			+reporttemplatecode+"&cond=to_idIS"+record.data['to_id']+"&formCondition=tr_idIS"+record.data['tr_id'];
		var keyField='tr_code';
		var KeyValue=record.data['tr_code']
		var title='产品培训考核报告';
		return '<a href="javascript:openUrl2(\''+url+'\',\''+title+'\',\''+keyField+'\',\''+KeyValue+'\');">' + KeyValue + '</a>';
	},
	CRMReport:function(val, meta, record,x,y){
		var me = this.RenderUtil || this;
		var reporttemplatecode=record.data['mr_reportcode'];
		var url='jsps/crm/marketmgr/marketresearch/researchReport.jsp?'+
		'whoami='+reporttemplatecode+'&cond=idIS'
		+record.data['id']+"&formCondition=mr_idIS"+record.data['mr_id']+'&gridCondition=mrd_mridIS'+record.data['mr_id'];
		var keyField='mr_code';
		var KeyValue=record.data['mr_code']
		var title='调研报告';
		return '<a href="javascript:openUrl2(\''+url+'\',\''+title+'\',\''+keyField+'\',\''+KeyValue+'\');">' + KeyValue + '</a>';
	},
	Vehicle:function(val, meta, record,x,y){
		var url='jsps/oa/vehicle/vehiclereturn.jsp?cond=va_code='+val;
		var title='返车维护单';
		var keyField='va_code';
		return '<a href="javascript:openUrl2(\''+url+'\',\''+title+'\',\''+keyField+'\',\''+val+'\');">' + val + '</a>';
	},
	/**
	 * 会议室申请，确认出席人员，如果已经确认出席的默认选上
	 */
	confirmMan:function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt;
		if(record.data['md_attend']=='是'){
			grid.selModel.select(record,true);
		}
		return val;
	},
	/**
	 * 会议室申请，确认出席人员，如果已经确认出席的默认选上
	 */
	oa_confirmMan:function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt;
		if(record.data['md_isconfirmed']=='-1'){
			grid.selModel.select(record,true);
		}
		return val;
	},	
	/**
	 * 通用方法
	 * 值=arg[0]-arg[1]-...
	 * @expression reduce:pd_qty:pd_yqty
	 * @expression reduce:pd_qty:100:pd_yqty
	 */
	reduce: function(val, meta, record, x, y, store, view){
		meta.tdCls = "x-grid-cell-renderer-bl";		
		var grid = view.ownerCt, me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex,
			format = (column.format || '0,000.00'), perc = format.substring(format.indexOf('.') + 1).length;
		if(!me || !me.args){
			grid = grid.ownerCt, me = grid.RenderUtil;
			if(!me || !me.args){
				return val;
			}
		}
		if(me.args){
			var arg = me.args.reduce[field];
			var maxValue = 0;
			if(arg && arg.length > 0){
				var v = 0;
				Ext.each(arg, function(a, index){
					if(Ext.isNumber(a)){
						v = a;
					} else {
						v = record.data[a] || 0;
					}
					if(index == 0){
						maxValue = Number(v);
					} else {
						maxValue -= Number(v);
					}
				});
			}


			//鉴于小数问题 保留2位小数
			//maxValue=maxValue.toFixed(4);
			if(column.editor || (column.getEditor && column.getEditor())) {//在允许编辑的情况下，修改值不能大于maxValue
				val = (!Ext.isNumber(val) || val == 0) ? maxValue : val;	
				if(Number(val) > Ext.Number.toFixed(maxValue, perc) ){
					val = maxValue;
					showError('请不要输入超过最大数量' + maxValue + "的值!");
				}
			} else {
				val = maxValue;
			}
			var f = Ext.Number.toFixed(val, perc), v = record.get(field), _v = val;
			if(column.xtype == 'numbercolumn'){
				_v = Ext.util.Format.number(val, column.format);
			}
			//f != val || f != v
			if( f != v) {
				record.set(field, f);
			}
			return _v; 
		} else {
			return val;
		}
	},
	/**
	 * @OA
	 * 信息发布 Note
	 */
	emergency: function(val, meta, record){
		var d = Number(val);
		var str = '';
		switch (d){
		case 1:
			str = '<span style="color:#20B7B9;padding-left:2px">一般</span>';
			break;
		case 2:
			str = '<img src="' + basePath + 'resource/images/renderer/remind.png">' + 
			'<span style="color:blue;padding-left:2px">平急</span>';
			break;
		case 3:
			str = '<img src="' + basePath + 'resource/images/renderer/remind2.png">' + 
			'<span style="color:red;padding-left:2px">特急</span>';
			break;
		}
		return str;
	},
	/**
	 * @OA流程催办
	 * */
	remindprocess:function(val,meta,record){
		var min=0,a=0,b=0,str='';
		if(record && record.data.jp_id){
			var launchTime=record.data.jp_launchTime;	
			min=parseInt((new Date().getTime()-new Date(launchTime).getTime())/60000);
			a=parseInt(min/60);
			b=min%60;
			if(a>0) str+=a+'小时';
			if(b>0) str+=b+'分钟';
			if(a>2) meta.style = 'color:red;';
			else meta.style='color:green';
			return str;
		}
		else return null;
	},

	/**
	 *@OA 版本管理
	 **/
	Version: function(val, meta, record){
		if(val == 0)
			return '当前版本|阅读版本';
		else  
			return '';
	},
	/**
	 *@OA 阅读状态
	 */
	State: function(val, meta, record){
		if(val == 0){
			var str = '<img src="' + basePath + 'resource/images/renderer/remind.png">' + 
			'<span style="color:blue;padding-left:2px">未阅</span>';
			return str ;
		} else  
			return '已阅';
	},
	/**
	 *@OA attentionGrade
	 */
	Grade:function(val,meta,record,x,y){
		var field = this.columns[y].dataIndex;
		var me = this.RenderUtil || this;
		var arg = me.args.Grade[field];
		var colorfield=arg[0];
		var color=record.data[colorfield];
		if(color) return '<span style="color:#'+color+'">'+val+'</span>';
	},
	GradeColor:function(val,meta,record){
		meta.style = "background:#"+val+";";
		return '';
	} ,   
	/**
	 *@OA 知识权限控制 
	 */
	OAHref: function(val, meta, record,x,y){
		var scanpersonid=record.data.kl_scanpersonid+'#';
		var authorid=record.data.kl_authorid;
		var me = this.RenderUtil || this;
		var field = this.columns[y].dataIndex;
		var KeyValue = 0;
		var url='';
		var title='';
		var arg = me.args.OAHref[field];
		var keyField=arg[0];
		var data=record.data;
		if(arg && arg.length > 0){
			KeyValue=data[keyField];
			url=arg[1];
			title=arg[2];
		}
		if(val==''||val==$I18N.common.grid.emptyText) return val;
		if(scanpersonid.indexOf(emid)>0||authorid==emid){
			return '<img src="' + basePath + 'resource/images/renderer/key2.png">'+'<span><a href="javascript:openUrl(' + KeyValue + ',\''+keyField+'\',\''+url+'\',\''+title+'\''+ ');">' + val + '</a></span>';
		}
		return '<img src="' + basePath + 'resource/images/renderer/key1.png">'+'<span style="color:#8B8B83;padding-left:2px "><a href="javascript:openWin();" style="text-decoration: none||blink;" active="color:#8B8B83; text-decoration:none;">' + val + '<a/></span>';
	},
	oa_oaacceptancedetail_status:function(val, meta, record){
		if(val=='1'){
			return '已转采购';
		}else{
			return '未转采购';
		}
	},
	/**
	 * @SCM.Purchase
	 * 采购明细数量的修改限制
	 */
	scm_purc_pdqty: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y];
		if(!Ext.isNumber(val)) {
			record.set('pd_qty', 0);
			return 0;
		} else {
			var adid = record.data['ad_qty'];oldqty = 0;
			if(adid != null && adid > 0 ) {
				if(val > adid) {
					oldqty = record.modified.pd_qty || record.data.pd_qty || adid;
					if (val != oldqty){
						val = oldqty; 
						record.set('pd_qty', oldqty); 
						showError('请不要超过请购数<' + adid + '>修改!');
					} 
				}
			}

			if(column.format)
				val = Ext.util.Format.number(val, column.format);
			return val;
		}
	},
	/**
	 * @PM.MakeNotice
	 * 制造通知单明细数量的修改限制
	 */
	pm_mano_mndqty: function(val, meta, record){
		meta.style = "background:#C6E2FF;";
		var sdid = record.data['sd_qty'];oldqty = 0;
		if(sdid != null && sdid > 0) {
			if(val > sdid) {
				oldqty = record.modified.mnd_qty || record.data.mnd_qty || sdid;
				val = oldqty;
				record.set('mnd_qty', oldqty);
				showError('请不要超过订单数<' + sdid + '>修改!');
			}
		}
		return val;
	},
	/**
	 * @SCM.Sale
	 * 销售通知单明细数量的修改限制
	 */
	scm_send_sndqty: function(val, meta, record){
		meta.style = "background:#C6E2FF;";
		var sdid = record.data['sd_qty'];oldqty = 0;
		if(sdid != null && sdid > 0) {
			if(val > sdid) {
				oldqty = record.modified.snd_outqty || record.data.snd_outqty || sdid;
				val = oldqty;
				record.set('snd_outqty', oldqty);
				showError('请不要超过订单数<' + sdid + '>修改!');
			}
		}
		return val;
	},
	/**
	 * @SCM.AcceptNotify
	 * 收料通知单明细数量的修改限制
	 */
	scm_acceptnotify_andinqty: function(val, meta, record){
		meta.style = "background:#C6E2FF;";
		var sdid = record.data['pd_qty'];oldqty = 0;
		if(sdid != null && sdid > 0) {
			if(val > sdid) {
				oldqty = record.modified.and_inqty || record.data.and_inqty || sdid;
				val = oldqty;
				record.set('and_inqty', oldqty);
				showError('请不要超过采购单数<' + sdid + '>修改!');
			}
		}
		return val;
	},
	/**
	 * @SCM.VerifyApply
	 * 收料单明细数量的修改限制
	 */
	scm_acceptnotify_vadqty: function(val, meta, record){
		meta.style = "background:#C6E2FF;";
		var pdqty = record.data['pd_qty'];oldqty = 0;
		if(pdqty != null && pdqty > 0) {
			if(val > pdqty) {
				oldqty = record.modified.vad_qty || record.data.vad_qty || pdqty;
				if(oldqty<=pdqty) {
					val = oldqty;
					record.set('vad_qty', oldqty);
					showError('请不要超过采购单数<' + sdid + '>修改!');
				}
			}
		}
		return val;
	},
	/**
	 * @SCM.ProdIODetail
	 * 出货单明细数量的修改限制
	 */
	scm_prodio_pdqty: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y];
		if(record.get('pd_snid') != null && record.get('pd_snid') != 0) {
			var snid = record.data['snd_outqty'];oldqty = 0;
			if(snid != null && snid > 0) {
				if(val > snid) {
					oldqty = record.modified.pd_outqty || record.data.pd_outqty || sdid;
					val = oldqty;
					record.set('pd_outqty', oldqty);
					showError('请不要超过通知单数<' + snid + '>修改!');
				}
			}
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		return val;
	},
	/**
	 * @PM.Dispatch
	 * 完成数不能大于流程单数量
	 */
	pm_dispatch_overqty: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y];
		var q = record.data['mf_qty'];
		if(q != null && q > 0) {
			if(val > q) {
				val = q;
				record.set('did_overqty', q);
				showError('完成数请不要超过流程单数量<' + q + '>!');
			}
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		return val;
	},
	/**
	 * @SCM.Sale.ReturnApply
	 * 退货申请单
	 */
	returnapply_qty: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y];
		var q = record.data['sd_sendqty'];
		if(q != null && q > 0) {
			if(val > q) {
				val = q;
				record.set('rad_qty', q);
				showError('请不要超过订单发货数<' + q + '>退货!');
			}
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		return val;
	},
	/**
	 * @SCM.ProdIODetail
	 * 采购验收单明细数量的修改限制
	 */
	scm_prodio_okqty: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y];
		if(record.get('pd_vacode') != null&&record.get('pd_vacode')!="") {
			var vadid = record.data['ve_okqty'];oldqty = 0;
			if(vadid != null && vadid > 0) {
				if(val > vadid) {
					oldqty = record.modified.pd_inqty || record.data.pd_inqty || vadid;
					val = oldqty;
					record.set('pd_inqty', oldqty);
					showError('请不要超过收料合格数<' + vadid + '>修改!');
				}
			}
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		return val;
	},
	/**
	 * 研发采购变更 限制数量
	 * @param val
	 * @param meta
	 * @param record
	 * @returns
	 */
	plm_pc_yqty: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y];
		var vadid = record.data['pcd_newqty'];
		if(vadid != null && vadid > 0) {
			if(val > vadid) {
				val = oldqty;
				record.set('pcd_newqty', val);
				showError('新采购数请不要小于已转数量<' + val + '>!');
			}
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		return val;
	},
	/**
	 * @SCM.ProdIODetail
	 * 不良品入库单明细数量的修改限制
	 */
	scm_prodio_ngqty: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y];
		if(record.get('pd_vacode') != null) {
			var vadid = record.data['ve_notokqty'];oldqty = 0;
			if(vadid != null && vadid > 0) {
				if(val > vadid) {
					oldqty = record.modified.pd_inqty || record.data.pd_inqty || vadid;
					val = oldqty;
					record.set('pd_inqty', oldqty);
					showError('请不要超过收料不合格数<' + vadid + '>修改!');
				}
			}
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		return val;
	},
	/**
	 * PM.makeScrap 生产报废单报废数量限制
	 */
	md_qty_mm_havegetqty: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y];
		var have = record.data['mm_havegetqty'] || 0,
		oldqty = 0;
		if(val > have) {
			oldqty = record.modified.md_qty || record.data.md_qty || have;
			val = oldqty;
			if(record.data.md_qty != oldqty) {
				record.set('md_qty', oldqty);
				showError('请不要超过已领数<' + have + '>!');
			}
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		return val;
	},
	/**
	 * @SCM.Sale.SaleChange
	 * 销售变更单新数量(原数量为0的时候不考虑)
	 */
	scm_sale_change_qty: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y];
		var adq = record.data['sd_qty'];
		var acq = record.data['sd_sendqty'];
		var yq = Math.min(record.data['sd_yqty'], adq);
		if(adq != null && adq > 0) {
			if(val > adq) {
				showError('请不要超过原订单数<' + adq + '>!');
				val = adq;
				record.set('scd_newqty', adq);
			}
		}
		if(acq != null && acq > 0) {
			if(val == 0) {
				val = acq;
				record.set('scd_newqty', acq);
			} else if(val < acq) {
				showError('请不要小于已发货数<' + acq + '>!');
				val = acq;
				record.set('scd_newqty', acq);
			}
		}
		if(yq != null && yq > 0) {
			if(val == 0) {
				val = yq;
				record.set('scd_newqty', yq);
			} else if(val < yq) {
				showError('请不要小于已转发货通知数<' + yq + '>!');
				val = yq;
				record.set('scd_newqty', yq);
			}
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		return val;

	},
	/**
	 * @SCM.Sale.SaleChange
	 * 销售变更单新数量(原数量为0的时候也要考虑)
	 */
	scm_sale_change_qty2: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y];
		var adq = record.data['sd_qty'];
		var acq = record.data['sd_sendqty'];
		var yq = Math.min(record.data['sd_yqty'], adq);
		if(adq != null && adq >= 0) {
			if(val > adq) {
				showError('请不要超过原订单数<' + adq + '>!');
				val = adq;
				record.set('scd_newqty', adq);
			}
		}
		if(acq != null && acq >= 0) {
			if(val == 0) {
				val = acq;
				record.set('scd_newqty', acq);
			} else if(val < acq) {
				showError('请不要小于已发货数<' + acq + '>!');
				val = acq;
				record.set('scd_newqty', acq);
			}
		}
		if(yq != null && yq >= 0) {
			if(val == 0) {
				val = yq;
				record.set('scd_newqty', yq);
			} else if(val < yq) {
				showError('请不要小于已转发货通知数<' + yq + '>!');
				val = yq;
				record.set('scd_newqty', yq);
			}
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		return val;

	},
	/**
	 * @SCM.Purchase.PurchaseChange
	 * 采购变更单新数量
	 */
	scm_purc_change_qty: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y];
		var adq = record.data['ad_qty'];oldqty = 0;
		var acq = record.data['pd_acceptqty'];
		var yq = record.data['pd_yqty'];
		if(adq != null && adq > 0) {
			if(val > adq) {
				val = adq;
				showError('请不要超过请购数<' + adq + '>!');
			}
		}
		if(acq != null && acq > 0) {
			if(val == 0) {
				val = acq;
			} else if(val < acq) {
				val = acq;
				showError('请不要小于验收数<' + acq + '>!');
			}
		}
		if(yq != null && yq > 0) {
			if(val == 0) {
				val = yq;
			} else if(val < yq) {
				val = yq;
				showError('请不要小于采购已转数<' + yq + '>!');
			}
		}
		if(record.get('pcd_newqty') != val) {
			record.set('pcd_newqty', val);
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		return val;
	},

	/**
	 * @SCM.Purchase.PurchaseChange
	 * 采购变更单新数量(易方)
	 */
	scm_purc_change_qty2: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y];
		var adq = record.data['pcd_oldqty'];oldqty = 0;
		var acq = record.data['pd_acceptqty'];
		var yq = record.data['pd_yqty'];
		if(adq != null && adq > 0) {
			if(val > adq) {
				val = adq;
				showError('请不要超过原采购订单数<' + adq + '>!');
			}
		}
		if(acq != null && acq > 0) {
			if(val == 0) {
				val = acq;
			} else if(val < acq) {
				val = acq;
				showError('请不要小于验收数<' + acq + '>!');
			}
		}
		if(yq != null && yq > 0) {
			if(val == 0) {
				val = yq;
			} else if(val < yq) {
				val = yq;
				showError('请不要小于采购已转数<' + yq + '>!');
			}
		}
		if(record.get('pcd_newqty') != val) {
			record.set('pcd_newqty', val);
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		return val;
	},
	/**
	 * @PM.Make.MakeChange
	 * 制造变更单新数量(易方)
	 */
	pm_make_change_qty: function(val, meta, record){
		meta.style = "background:#C6E2FF;";
		var adq = record.data['md_qty'];oldqty = 0;
		var acq = record.data['ma_madeqty'];
		var yq = record.data['ma_toquaqty'];
		if(adq != null && adq > 0) {
			if(val > adq) {
				val = adq;
				showError('请不要超过原制造单数<' + adq + '>!');
			}
		}
		if(acq != null && acq > 0) {
			if(val == 0) {
				val = acq;
			} else if(val < acq) {
				val = acq;
				showError('请不要小于已完工数<' + acq + '>!');
			}
		}
		if(yq != null && yq > 0) {
			if(val == 0) {
				val = yq;
			} else if(val < yq) {
				val = yq;
				showError('请不要小于已转检验数<' + yq + '>!');
			}
		}
		if(record.get('md_newqty') != val) {
			record.set('md_newqty', val);
		}
		return val;
	},
	/**
	 * @PM.Make.MakeChangeOS
	 * 委外变更单新数量(易方)
	 */
	pm_makeos_change_qty: function(val, meta, record){
		meta.style = "background:#C6E2FF;";
		var adq = record.data['md_oldqty'];oldqty = 0;
		var acq = record.data['ma_madeqty'];
		var yq = record.data['ma_haveqty'];
		if(adq != null && adq > 0) {
			if(val > adq) {
				val = adq;
				showError('请不要超过原委外单数<' + adq + '>!');
			}
		}
		if(acq != null && acq > 0) {
			if(val == 0) {
				val = acq;
			} else if(val < acq) {
				val = acq;
				showError('请不要小于已生产数量<' + acq + '>!');
			}
		}
		if(yq != null && yq > 0) {
			if(val == 0) {
				val = yq;
			} else if(val < yq) {
				val = yq;
				showError('请不要小于已转收料数<' + yq + '>!');
			}
		}
		if(record.get('md_newqty') != val) {
			record.set('md_newqty', val);
		}
		return val;
	},
	/**
	 * @SCM.Sale.SendNotify
	 * 出货通知变更单新数量
	 */
	scm_sendnotify_change_qty: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y];
		var adq = record.data['scd_oldqty'];
		var yq = Math.min(record.data['snd_yqty'], adq);
		if(adq != null && adq > 0) {
			if(val > adq) {
				showError('请不要超过原通知单数<' + adq + '>!');
				val = adq;
				record.set('scd_qty', adq);
			}
		}
		if(yq != null && yq > 0) {
			if(val == 0) {
				val = yq;
				record.set('scd_qty', yq);
			} else if(val < yq) {
				showError('请不要小于已转出货数<' + yq + '>!');
				val = yq;
				record.set('scd_qty', yq);
			}
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		return val;

	},
	/**
	 * @SCM.Sale.SaleForecastChange
	 * 预测变更单明细数量的修改限制
	 */
	scm_sale_sfchange_qty: function(val, meta, record){
		meta.style = "background:#C6E2FF;";
		var sdq = record.data['scd_oldqty'];
		var kq = record.data['scd_minqty'];
		if(sdq != null && sdq > 0) {
			if(val > sdq) {
				val = sdq;
				showError('请不要超过原预测数量<' + sdq + '>!');
			}
		}
		if(kq != null && kq > 0) {
			if(val == 0) {
				val = kq;
			} else if(val < kq) {
				val = kq;
				showError('请不要小于最小变更数<' + kq + '>!');
			}
		}
		if(record.get('scd_newqty') != val) {
			record.set('scd_newqty', val);
		}
		return val;
	},
	/**
	 * @SCM.Sale
	 * 销售单明细数量的修改限制
	 */
	scm_sale_sdqty: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var status = Ext.getCmp('sa_statuscode') == null ? null : Ext.getCmp('sa_statuscode').value;
		var  grid = view.ownerCt,column = grid.columns[y];
		var maxValue = record.modified.sd_qty || record.data.sd_qty;
		if(status == null || status == 'ENTERING' || status == 'COMMITED'){
			var sd_sourceid = record.data['sd_sourceid'];
			if(sd_sourceid != null && sd_sourceid != '0' && sd_sourceid != 0){//有来源
				//限制val<maxValue
				if(val > maxValue){
					val = maxValue;
					record.set('sd_qty', val);
					showError('请不要输入超过来源数量' + maxValue + "的值!");
				}
			}
			if(!Ext.isNumber(val)){
				val = maxValue;
				record.set('sd_qty', val);
				showError('请输入数字!');
			}
			if(val < 0){
				val = maxValue;
				record.set('sd_qty', val);
				showError("请不要输入小于0的值!");
			}
		} else {
			if(val != maxValue){
				val = maxValue;
				record.set('sd_qty', val);
				showError("该单据已审核,无法修改,请申请变更!");
			}
		}
		if(column.xtype == 'numbercolumn' && val != 0){
			val = Ext.util.Format.number(val, column.format);
		}
		return val;
	},
	/**
	 * @SCM.Purchase.VerifyApply
	 * 收料单明细数量的修改限制
	 */
	scm_purc_vadqty: function(val, meta, record){
		meta.style = "background:#C6E2FF;";
		var andid = record.data['vad_andid'];
		if(andid == null || andid == 0){
			var pdid = record.data['pd_qty'];
			var qty = 0;
			if(pdid != null && pdid > 0 && val > pdid) {
				if(val > pdid) {
					qty = pdid || record.modified.vad_qty || record.data.vad_qty;
					val = qty;
					record.set('vad_qty', qty);
					showError('请不要超过采购数<' + pdid + '>修改!');
				}
			}
		} else {
			var andqty = record.data['and_inqty'],oldqty = 0;
			if(andqty != null && andqty > 0) {
				if(val > andqty) {
					oldqty = record.modified.vad_qty || record.data.vad_qty || andqty;
					val = oldqty;
					record.set('vad_qty', oldqty);
					showError('请不要超过收料通知单数<' + andqty + '>修改!');
				}
			}
		}
		return val;
	},
	/**
	 * 通用方法
	 * 仅仅是改变该列的背景颜色
	 * @arg red/black.../C6E2FF/B376F5/..null..
	 * @expression color:red
	 * @expression color:#C1D0D9
	 */
	color: function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		if(!me || !me.args){
			meta.style = "background:#C6E2FF;";
			return val;
		}
		me.args.color = me.args.color || new Object();
		var arg = me.args.color[field];
		if(arg && arg.length > 0){
			meta.style = "background:" + arg[0] + ";";
		} else {
			meta.style = "background:#C6E2FF;";
		}
		if(column.xtype == 'numbercolumn' && val != 0){
			val = Ext.util.Format.number(val, column.format);
		}
		return val;
	},
	/**
	 * 通用方法
	 * 如果当前字段的值为某个值时，或者当前字段的值不为某个值时，改变该单元格所在行的背景颜色
	 * @Expression cellbgcolor:N:0:red 单元格内容不等于0整行显示红色
	 * @Expression cellbgcolor:0:red 单元格内容等于0整行显示红色
	 */
	cellbgcolor:function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		if(!me || !me.args){
			meta.style = "background:#C6E2FF;";
			return val;
		}
		me.args.cellbgcolor = me.args.cellbgcolor || new Object();
		var arg = me.args.cellbgcolor[field];
		if(arg && (arg.length == 2)){
			if(arg[0]==val){
				view.getRowClass = function(record, rowIndex, rowParams, store) {	
					if(record.get(field)==arg[0]){
						return arg[1];		            
					}
		        };	
			}
		}else if(arg && (arg.length == 3)){
			if(arg[1]!=val){
				view.getRowClass = function(record, rowIndex, rowParams, store) {	
					if(record.get(field)!=arg[1]){
						return arg[2];		
					}
		        };	
			}
		} else {
			meta.style = "background:#C6E2FF;";
		}
		if(column.xtype == 'numbercolumn' && val != 0){
			val = Ext.util.Format.number(val, column.format);
		}
		return val;
	},
	
		/**
	 * 
	 * 通用方法
	 * 如果当前字段的值为在集合中的某个值(模糊),加粗该单元格
	 * @expression cellBold:{合计,小计,可自由动用的现金余额,资金余额}
	 */
	cellBold:function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		var arg = me.args.cellBold[field];
		var str,array = new Array(),count=0;
		if (arg) {
			str=arg[0].replace("{","").replace("}","");
			array=str.split(",");
			for (var i = 0; i < array.length; i++) {
				if (val.indexOf(array[i])>-1) {
					count=count+1;
				}
			}
			if (count>0) {
				val='<b>'+val+'</b>';
			}
			
		}
		return val;
	},
	
	/**
	 * 通用方法
	 * 如果当前字段的值为空或空字符串,就获取指定字段的值赋给当前字段
	 * @expression copy:pcd_oldprodcode
	 */
	copy: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		if(val == null || val == '' || val == 0){
			if(!me || !me.args){
				grid = grid.ownerCt, me = grid.RenderUtil;
				if(!me || !me.args){
					return val;
				}
			}
			var v = record.data[field];
			if(v == null || v == '' || (column.xtype == 'numbercolumn' && v == 0)){
				var arg = me.args.copy[field];
				if(arg && arg.length > 0){
					val = record.data[arg[0]];
				}
				if(val != null && val != '' && val != 0){
					record.set(field, val);
				}
			}
		}
		if(column.xtype == 'numbercolumn' && val != 0){
			val = Ext.util.Format.number(val, column.format);
		}
		if(column.xtype == 'datecolumn') {
			if(!val) return null;
			val =  Ext.Date.format(val, (column.format || 'Y-m-d'));
		}
		return val; 
	},
	/**
	 * 通用方法
	 * 如果指定字段的值为空或空字符串，就获取当前值赋给指定字段
	 * @expression paste:pcd_newprodcode
	 */
	paste: function(val, meta, record, x, y, store, view){
		meta.style = "background:#C6E2FF;";
		if(val != null && val != ''){
			var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
			if(!me || !me.args){
				grid = grid.ownerCt, me = grid.RenderUtil;
				if(!me || !me.args){
					return val;
				}
			}
			var arg = me.args.paste[field];
			var value = null;
			if(arg && arg.length > 0){
				value = record.data[arg[0]];
				if(value == null || value == ''){
					if(column.xtype == 'numbercolumn' && val != 0){
						record.set(arg[0], val);
					} else {
						if(Ext.isDate(val)){
							val = Ext.Date.toString(val);
						}
						record.set(arg[0], val); 
					}
				}
			}
		}
		return val; 
	},
	/**
	 * 必填字段
	 */
	necessary: function(val, meta, record, x, y, store, view){
		return '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">' + 
		'<span style="color:blue;padding-left:2px;" title="必填字段">' + val + '</span>';
	},
	/**
	 * @FA
	 * 应收系统参数设置
	 */
	stringToDate: function(val, meta, record, x, y){
		meta.style = "background:#C6E2FF;";
		var reg = /\d{2}-\d{1,2}月-\d{2}/;
		var field = this.columns[y].dataIndex;

		if(val == 'true'){
			record.set(field,'是');
		}
		if(val == 'false'){
			record.set(field,'否');
		}
		if(reg.test(val)){
			var day = val.split('-')[0];
			var month = val.split('-')[1].replace('月', '');
			if(Number(month) < 10){
				month = '0' + month;
			}
			var year = '20' + val.split('-')[2];
			val = year + '-' + month + '-' + day;
			record.set(field, val);

		}
		return val;
	},
	/**
	 * @PM.Make
	 * 成套发料,本次发料套数
	 */
	pm_make_issue_thisqty: function(val, meta, record, x, y){
		var code = record.data['ma_code'], busy = Ext.getCmp('grid').busy;
		record.hasdataChanged=false;
		if(!Ext.isEmpty(record.data['ma_id']) && !Ext.isEmpty(code)) {		
			var nQty = record.data['ma_qty'] - record.data['ma_haveqty'];
			if(record.maxQty!=nQty){
				record.maxQty=nQty;
				record.hasdataChanged=true;
			}
			var maxQty = nQty;
			if(val > maxQty) {
				if(!busy && !record.hasdataChanged)
					showError('本次发料套数不能超过<' + maxQty + '>,单号:' + code);
				val = maxQty;
				if(record.data['ma_thisqty'] != val) {
					Ext.defer(function(){
						record.set('ma_thisqty', val);
					},10);
				}
			} else{
				if(record.hasdataChanged) val=maxQty;
				val= val==0?maxQty:val;
				if(record.data['ma_thisqty'] != val) {		
					//4.2 set冲突
					Ext.defer(function(){
						record.set('ma_thisqty', val);
					},10);
				}
			}
			var grid = Ext.getCmp('editorColumnGridPanel'),items = grid.store.data.items;
			if(Ext.getCmp('set').value) {
				Ext.each(items, function(item){
					if(item.data['mm_code'] == code){
						var max = item.data['mm_oneuseqty'] * val;
						//判断单位用量的小数位数,本次领料的小数位数与单位用量的相同
						var length = item.data['mm_oneuseqty'].toString().split(".")[1]?item.data['mm_oneuseqty'].toString().split(".")[1].length:0;
						max = max.toFixed(length);
						var t = 0;
						if(item.data.isrep) {
							t = item.data['mm_canuserepqty'] - item.data['mm_totaluseqty'];
						} else {
							t = item.data['mm_qty'] - item.data['mm_canuserepqty'] - (item.data['mm_havegetqty'] + 
									item.data['mm_returnmqty'] - item.data['mm_addqty'] - item.data['mm_haverepqty']) - 
									item.data['mm_totaluseqty'];
						}
						t = t.toFixed(length);
						max = Math.min(t, max);
						if(item.data['mm_thisqty'] != max){
							item.set('mm_thisqty', max);
						}
					}
				});
			}
		}
		return val;
	},
	/**
	 * @PM.Make
	 * 制造单本次领料数mm_thisqty=mm_qty(制单数)-mm_canuserepqty(替代维护数)-mm_havegetqty(已领)+mm_returnmqty(不良数)
	 * 	-mm_addqty(补料数)-mm_haverepqty(替代已领)-mm_totaluseqty(已转领料)
	 */
	pm_make_thisqty: function(val, meta, record, x, y){
		var ta1=0;
		meta.tdCls = "x-grid-cell-renderer-bl";
		if(record.data['mm_balance']){
			var balance = record.data['mm_balance'].toString().split(".");
			ta1 = balance[1]?balance[1].length:0;
		}
		var t = 0,r=0,busy = Ext.getCmp('editorColumnGridPanel').busy;
		var ta = record.data['mm_oneuseqty'].toString().split(".");
    	var length = Math.max((ta[1]?ta[1].length:0), ta1);
		//r为该序号总剩余未发料数
		r = record.data['mm_qty'] - (record.data['mm_havegetqty'] - record.data['mm_addqty'] + record.data['mm_returnmqty'] )  - record.data['mm_totaluseqty']  ; 
		if(record.data.isrep) {
			t = record.data['mm_qty'] - (record.data['mm_havegetqty'] - record.data['mm_addqty'] + record.data['mm_returnmqty'] )  - record.data['mm_totaluseqty'] ; 
		} else {
			t = record.data['mm_qty'] - record.data['mm_canuserepqty'] - (record.data['mm_havegetqty'] - record.data['mm_haverepqty'] + 
					record.data['mm_returnmqty'] - record.data['mm_repreturnmqty'] - record.data['mm_addqty'] + record.data['mm_repaddqty']) - 
					record.data['mm_totaluseqty']+record.data['mm_repqty']; 
		}		
		//判断单位用量的小数位数,本次退料的小数位数与单位用量的相同
      	t = t.toFixed(length)-0;
      	r = r.toFixed(length)-0;
        //大于总剩余未发料数则默认等于剩余未发料数
        t = t > r ? r : t;
		t = t < 0 ? 0 : t;
		var max = t;
		max = max < 0 ? 0 : max;
		//ma_thisqty*mm_oneuseqty
		var tqty = max;
		if(Ext.getCmp('set') //form+detail 发料界面 没有这个字段
				&& Ext.getCmp('set').value) {
			var items = Ext.getCmp('grid').store.data.items,mItem = null;
			if(ifIncludingLoss){//套料发料包含损耗
				Ext.each(items, function(item){
					if(item.data['ma_code'] != null && item.data['ma_code'].toString().trim() != '' 
						&& item.data['ma_code'] == record.data['mm_code']){
						mItem = item;
						if(item.data['ma_thisqty'] > 0){
							max = item.data['ma_thisqty'] * record.data['mm_qty']/item.data['ma_qty'];
							if(parseInt(record.data['mm_qty'])== record.data['mm_qty']){//需求数为整数，取整
								max = Math.ceil(max);
							}
							max = max.toFixed(length);
							tqty = Math.min(max, t);
							tqty = Number(tqty);
						}
					}
			    });
			}else{
				Ext.each(items, function(item){
					if(item.data['ma_code'] != null && item.data['ma_code'].toString().trim() != '' 
						&& item.data['ma_code'] == record.data['mm_code']){
						mItem = item;
						if(item.data['ma_thisqty'] > 0 && record.data['mm_oneuseqty'] > 0){
							max = item.data['ma_thisqty'] * record.data['mm_oneuseqty'];
							max = max.toFixed(length);
							tqty = Math.min(max, t);
							tqty = Number(tqty);
						}
					}
			    });
			}						
		}
		val = Number(val);
		if(!record.dirty){//未被修改过，并且mm_thisqty与计算值不等
		  if(val != tqty){
			val = tqty;
			Ext.defer(function(){
				record.set('mm_thisqty', val);
			},10);
		  }
		}else{
		     if(val > tqty) {
				//如果参数设置为不考虑可替代数
				if (ifCanrepqty != true){
					if(!busy)
						showError("本次领料数不得超过" + tqty);
						record.set('mm_thisqty', tqty);
				}else{
					if(val > r){			
						showError("本次领料数不得超过" + r);
						record.set('mm_thisqty', r);
					}
				}
				val = tqty;
				Ext.defer(function(){
					record.set('mm_thisqty', val);
				},10);
			} else if(val < 0){
				if(!busy)
					showError("本次领料数不能是负数");
				val = tqty;
				Ext.defer(function(){
					record.set('mm_thisqty', val);
				},10);
			} else if(val == 0 && tqty != 0){
				val = tqty;
				Ext.defer(function(){
					record.set('mm_thisqty', val);
				},10);
			} else {
	//			if(val < max && mItem) {//本次领料数 <本次发料套数*单位用量
	//			mItem.maxQty = Math.ceil(val/record.data['mm_oneuseqty']);
	//			mItem.set('ma_thisqty', mItem.maxQty);
	//			}
			}
		}
		return val;
	},
	/**
	 * 添加锁定库存数判断 欧盛
	 */
	pm_make_thisqtyosdb: function(val, meta, record, x, y){
		meta.tdCls = "x-grid-cell-renderer-bl";
		var t = 0,r=0,busy = Ext.getCmp('editorColumnGridPanel').busy;
		var ta = record.data['mm_oneuseqty'].toString().split(".");
    	var length = ta[1]?ta[1].length:0;
		//r为该序号总剩余未发料数
		r = record.data['mm_qty'] - (record.data['mm_havegetqty'] - record.data['mm_addqty'] + record.data['mm_returnmqty'] )  - record.data['mm_totaluseqty']  ; 
		if(record.data.isrep) {
			t = record.data['mm_qty'] - (record.data['mm_havegetqty'] - record.data['mm_addqty'] + record.data['mm_returnmqty'] )  - record.data['mm_totaluseqty'] ; 
		} else {
			t = record.data['mm_qty'] - record.data['mm_canuserepqty'] - (record.data['mm_havegetqty'] - record.data['mm_haverepqty'] + 
					record.data['mm_returnmqty'] - record.data['mm_repreturnmqty'] - record.data['mm_addqty'] + record.data['mm_repaddqty']) - 
					record.data['mm_totaluseqty']+record.data['mm_repqty']; 
		}		
		//判断单位用量的小数位数,本次退料的小数位数与单位用量的相同
      	t = t.toFixed(length) -0 ;
      	r = r.toFixed(length) -0 ;
        //大于总剩余未发料数则默认等于剩余未发料数
        t = t > r ? r : t;
		t = t < 0 ? 0 : t;
		var max = t;
		max = max < 0 ? 0 : max;
		//ma_thisqty*mm_oneuseqty
		var tqty = max;
		if(Ext.getCmp('set').value) {
			var items = Ext.getCmp('grid').store.data.items,mItem = null;
			if(ifIncludingLoss){//套料发料包含损耗
				Ext.each(items, function(item){
					if(item.data['ma_code'] != null && item.data['ma_code'].toString().trim() != '' 
						&& item.data['ma_code'] == record.data['mm_code']){
						mItem = item;
						if(item.data['ma_thisqty'] > 0){
							max = item.data['ma_thisqty'] * record.data['mm_qty']/item.data['ma_qty'];
							if(parseInt(record.data['mm_qty'])== record.data['mm_qty']){//需求数为整数，取整
								max = Math.ceil(max);
							}
							max = max.toFixed(length);
							tqty = Math.min(max, t);
							tqty = Number(tqty);
						}
					}
			    });
			}else{
				Ext.each(items, function(item){
					if(item.data['ma_code'] != null && item.data['ma_code'].toString().trim() != '' 
						&& item.data['ma_code'] == record.data['mm_code']){
						mItem = item;
						if(item.data['ma_thisqty'] > 0 && record.data['mm_oneuseqty'] > 0){
							max = item.data['ma_thisqty'] * record.data['mm_oneuseqty'];
							max = max.toFixed(length);
							tqty = Math.min(max, t);
							tqty = Number(tqty);
						}
					}
			    });
			}						
		}
		tqty = Math.min(tqty,(record.data["mm_assignqty"]== undefined?0:record.data["mm_assignqty"]));
		val = Number(val);
		if(!record.dirty){//未被修改过，并且mm_thisqty与计算值不等
		  if(val != tqty){
			val = tqty;
			Ext.defer(function(){
				record.set('mm_thisqty', val);
			},10);
		  }
		}else{
		     if(val > tqty) {
				//如果参数设置为不考虑可替代数
				if (ifCanrepqty != true){
					if(!busy)
						showError("本次领料数不得超过" + tqty);
				}else{
					if(val > r){			
						showError("本次领料数不得超过" + r);
					}
				}
				val = tqty;
				Ext.defer(function(){
					record.set('mm_thisqty', val);
				},10);
			} else if(val < 0){
				if(!busy)
					showError("本次领料数不能是负数");
				val = tqty;
				Ext.defer(function(){
					record.set('mm_thisqty', val);
				},10);
			} else if(val == 0 && tqty != 0){
				val = tqty;
				Ext.defer(function(){
					record.set('mm_thisqty', val);
				},10);
			} else {
				//判断remain
				if(val>(record.data["mm_assignqty"]== undefined?0:record.data["mm_assignqty"])){
					showError("本次发料数量不能大于锁定库存数");
					val = tqty;
					Ext.defer(function(){
						record.set('mm_thisqty', val);
					},10);
				}
			}
		}
		return val;
	},
	/**
	 * 添加check方法 MRP需求投放界面的建议变更数
	 */
	check: function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		var a = view.panel.editingPlugin.events.validateedit;
		var editvalue=val;
		var m = record.modified[field];
		if(!me || !me.args){
			grid = grid.ownerCt, me = grid.RenderUtil;
			if(!me || !me.args){
				return val;
			}
		}
		var arg = me.args.check[field];
		var flag;
		if(arg && arg.length > 0){
			var data = record.data,keys = Ext.Object.getKeys(data),formu =arg[0];
			Ext.each(keys, function(k){
				if(contains(formu, k, true)) {
					if(typeof data[k] == "number"){
						formu = formu.replace(new RegExp(k,"gm"),data[k]);
					}else if(typeof data[k] == "string"){
						formu = formu.replace(new RegExp(k,"gm"), "'" + data[k] + "'");
					}
				}
			});
			var d = 0;
			var dirty =record.dirty;
			var a = Ext.util.Format.number(val, column.format);
			var b = Ext.util.Format.number(d, column.format);
			try {
//				formu = formu.replace("=","==");
//				formu = formu.replace("and","&&");
				d = eval(formu);
				if(d && dirty && record.modified[field]!=val){
					showError("修改值超可修改范围");
					grid.editingPlugin.suspendEvents(false);
					Ext.defer(function(){
						record.set(field, m);
					},10);
					return record.modified[field];
				}else{
					grid.editingPlugin.resumeEvents(); 
					return val;
				}
			} catch (e) {
				d = 0;
			}			
		}
		if(record.get(field) != 0){
			return val;
		}
		return "";
	},
	/**
	 * @PM.Make
	 * 替代料，特殊样式
	 */
	pm_isrep: function(val, meta, record, x, y){
		//区分4.2版本
		if(this.isLocked && this.ownerCt && this.ownerCt.version=='4.2'){
			y=y-1;
		}
		var field = this.columns[y].dataIndex;
		if(record.data.isrep) {
			meta.tdCls = "x-grid-cell-renderer-cl";
			if(field == 'mm_code') {
				return '<img src="' + basePath + 'resource/images/renderer/important.png">' + 
				'替代料';
			} else {
				return val;
			}
		}
		return val;
	},
	/**
	 * @PM.Make
	 * 成套退料,本次退料套数 成套报废，本次报废套数
	 */
	pm_make_return_thisqty: function(val, meta, record, x, y){
		var code = record.data['ma_code'], busy = Ext.getCmp('grid').busy;
		record.hasdataChanged=false;
		if(!Ext.isEmpty(record.data['ma_id']) && !Ext.isEmpty(code)) {
			var nQty = record.data['ma_qty'];
			if(!record.maxQty || record.maxQty!=nQty){
				record.maxQty=nQty;
				record.hasdataChanged=true;
			}
			var maxQty = Math.min(record.maxQty, nQty);
			if(val > maxQty) {
				if(!busy && !record.hasdataChanged)
					showError('套数不能超过<' + maxQty + '>,单号:' + code);
				val = maxQty;
				if(record.data['ma_thisqty'] != val) {
					record.set('ma_thisqty', val);
				}
			} else if(val < 0){
				val= 0;
				if(record.data['ma_thisqty'] != val) {
					record.set('ma_thisqty', val);
				}
			}
			if((!Ext.isEmpty(record.data['ma_remainqty']) && record.data['ma_remainqty'] == 0 )|| Ext.isEmpty(record.data['ma_remainqty'])){
				var grid = Ext.getCmp('editorColumnGridPanel'),items = grid.store.data.items;
				Ext.each(items, function(item){
					if(item.data['mm_code'] == code){
						var max = item.data['mm_oneuseqty'] * val;
						//判断单位用量的小数位数,本次退料的小数位数与单位用量的相同
						var length = item.data['mm_oneuseqty'].toString().split(".")[1]?item.data['mm_oneuseqty'].toString().split(".")[1].length:0;
						max = max.toFixed(length);
						var t = item.data['mm_onlineqty'] || item.data['mm_thisqty'];
						max = Math.min(t, max);
						if(item.data['mm_thisqty'] != max){
							item.set('mm_thisqty', max);
						}
					}
				}); 
			}
		}
		/*if(val>0){
			record.data.ma_remainqty = 0;
		}*/
		return val;
	},
	/**
	 * @PM.Make
	 * 成套退料,工单保留套数
	 */
	pm_make_return_remain: function(val, meta, record, x, y){
		var code = record.data['ma_code'], busy = Ext.getCmp('grid').busy, pr_lossrate;
		record.hasdataChanged=false;
		if(!Ext.isEmpty(record.data['ma_id']) && !Ext.isEmpty(code)) {
			var nQty = record.data['ma_qty'];
			if(!record.maxQty || record.maxQty!=nQty){
				record.maxQty=nQty;
				record.hasdataChanged=true;
			}
			var maxQty = Math.min(record.maxQty, nQty);
			if(val > maxQty) {
				if(!busy && !record.hasdataChanged)
					showError('套数不能超过<' + maxQty + '>,单号:' + code);
				val = maxQty;
				if(record.data['ma_remainqty'] != val) {
					record.set('ma_remainqty', val);
				}
			} else if(val < 0){
				val= 0;
				if(record.data['ma_remainqty'] != val) {
					record.set('ma_remainqty', val);
				}
			}
			if(val >0){
				var grid = Ext.getCmp('editorColumnGridPanel'),items = grid.store.data.items;
				var remainqty = record.data['ma_remainqty'] ;
				Ext.each(items, function(item){
					if(item.data['mm_code'] == code){
						if(item.data['pr_lossrate']){
							pr_lossrate = item.data['pr_lossrate'];
						}else{
							pr_lossrate = 0;
						}
						if(item.data['mm_qty']==Math.floor(item.data['mm_qty'])){
							var max = Math.floor(item.data['mm_havegetqty']+item.data['mm_totaluseqty']-item.data['mm_scrapqty']-item.data['mm_oneuseqty']*remainqty*(1+pr_lossrate/100));
							var t = item.data['mm_onlineqty'] || item.data['mm_thisqty'];
							max = Math.min(t, max);
							if(item.data['mm_thisqty'] != max){
								item.set('mm_thisqty', max);
							}
						}else{
							var max = item.data['mm_havegetqty']+item.data['mm_totaluseqty']-item.data['mm_scrapqty']-item.data['mm_oneuseqty']*remainqty*(1+pr_lossrate/100);
							//判断单位用量的小数位数,本次退料的小数位数与单位用量的相同
							var length = item.data['mm_qty'].toString().split(".")[1]?item.data['mm_qty'].toString().split(".")[1].length:0;
							max = max.toFixed(length);
							var t = item.data['mm_onlineqty'] || item.data['mm_thisqty'];
							max = Math.min(t, max);
							if(item.data['mm_thisqty'] != max){
								item.set('mm_thisqty', max);
							}
						}
					}
				}); 
			}
		}
		return val;
	},
	/**
	 * @PM.Make
	 * 制造单本次退料数mm_thisqty<=mm_onlineqty(在制数) ，本次报废数
	 */
	pm_make_rqty: function(val, meta, record, x, y, store){
		meta.style = "background:#C6E2FF;";
		if(record.data['mm_onlineqty'] < 0) {// 返修工单退料
			return val;
		}
		var mrec = record;
		if(record.data.isrep) {
			//替代料 本次数量 按主料本次数量计算
			store.each(function(d){
				if(d.data.mm_id == record.data.mm_id && !d.data.isrep) {
					mrec = d;return;
				}
			});
		}
		  //判断单位用量的小数位数,本次退料的小数位数与单位用量的相同
		var length = mrec.data['mm_oneuseqty'].toString().split(".")[1]?mrec.data['mm_oneuseqty'].toString().split(".")[1].length:0;
		var busy = Ext.getCmp('editorColumnGridPanel').busy,t;
		var allowChangeAfterCom= Ext.getCmp('allowChangeAfterCom').value;
		if(allowChangeAfterCom){
			t = Math.floor(((mrec.data['mm_havegetqty'] || 0) - (mrec.data['mm_scrapqty'] || 0)  - (mrec.data['mm_backqty'] || 0) - (mrec.data['mm_turnscrapqty'] || 0)));
			t = Math.min(t,val);
		}else{
			t = (( mrec.data['mm_onlineqty']|| val) - (mrec.data['mm_backqty'] || 0));
			t = Math.min(t,val);
		}
		t = t.toFixed(length);
		var max = t;
		max = max < 0 ? 0 : max;
		var tqty = max;
		var items = Ext.getCmp('grid').store.data.items;
		Ext.each(items, function(item){
			  if(item.data['ma_code'] != null && item.data['ma_code'].toString().trim() != '' 
				  && item.data['ma_code'] == mrec.data['mm_code']){
				  mItem = item;
				  if(item.data['ma_thisqty'] > 0 && mrec.data['mm_oneuseqty'] > 0){
					  max = item.data['ma_thisqty'] * mrec.data['mm_oneuseqty'];
					  max = max.toFixed(length);
					  tqty = Math.min(max, t);
				  }else if(item.data['ma_remainqty'] > 0 && mrec.data['mm_oneuseqty'] > 0){
					  var nQty = item.data['ma_remainqty'];
					  //备损率算法  
					  var pr_lossrate = (mrec.data["mm_qty"]-item.data["ma_qty"]*mrec.data["mm_oneuseqty"])/(mrec.data["mm_oneuseqty"]*item.data["ma_qty"]);
					  /*if(mrec.data['pr_lossrate']){
						  pr_lossrate = mrec.data['pr_lossrate'];
					  }else{
						  pr_lossrate = 0;
					  }*/
					  if(mrec.data['mm_qty']==Math.floor(mrec.data['mm_qty'])){
						  max = Math.floor(mrec.data['mm_havegetqty']+mrec.data['mm_totaluseqty']-mrec.data['mm_scrapqty']-mrec.data['mm_oneuseqty']*nQty*pr_lossrate);
					  }else{
						  max = mrec.data['mm_havegetqty']+mrec.data['mm_totaluseqty']-mrec.data['mm_scrapqty']-mrec.data['mm_oneuseqty']*nQty*pr_lossrate;
						  max = max.toFixed(length);						 
					  }
					  max = max<0?0:max;
					  tqty = Math.min(max, t);
				  }
			  }
		});	
		if(val > tqty) {
			if(!busy)
				showError("本次填写的数量不得超过" + tqty);
			val = tqty;
			record.set('mm_thisqty', val);
		} else if(val < 0){
			if(!busy)
				showError("本次填写的数量不能是负数");
			val = tqty;
		} else if(val == 0 && tqty != 0){
			val = tqty;
		}
		if(val && record.get('mm_thisqty') != val){
			
			record.set('mm_thisqty', val);
		}
		return val;
	},
	/**
	 * @PM.Make  万利达，UAS 先用本次退料数
	 * 制造单本次退料数mm_thisqty<=mm_onlineqty(在制数) 
	 */
	pm_make_rqtyf: function(val, meta, record, x, y, store){
		meta.style = "background:#C6E2FF;";
		if(record.data['mm_onlineqty'] < 0) {// 返修工单退料
			return val;
		}
		var mrec = record;
		if(record.data.isrep) {
			//替代料 本次数量 按主料本次数量计算
			store.each(function(d){
				if(d.data.mm_id == record.data.mm_id && !d.data.isrep) {
					mrec = d;return;
				}
			});
		}
		 //判断单位用量的小数位数,本次退料的小数位数与单位用量的相同
		var length = mrec.data['mm_oneuseqty'].toString().split(".")[1]?mrec.data['mm_oneuseqty'].toString().split(".")[1].length:0;
		var busy = Ext.getCmp('editorColumnGridPanel').busy,t;
		var allowChangeAfterCom= Ext.getCmp('allowChangeAfterCom').value;
		if(allowChangeAfterCom){
			t = Math.floor(((mrec.data['mm_havegetqty'] || 0) - (mrec.data['mm_scrapqty'] || 0)  - (mrec.data['mm_backqty'] || 0) - (mrec.data['mm_turnscrapqty'] || 0)));
			t = Math.min(t,val);
		}else{
			t = ((mrec.data['mm_onlineqty'] || val) - (mrec.data['mm_backqty'] || 0));
			t = Math.min(t,val);
		}
		t = t.toFixed(length);
		var max = t;
		max = max < 0 ? 0 : max;
		var tqty = max;
		var items = Ext.getCmp('grid').store.data.items;
		Ext.each(items, function(item){
			  if(item.data['ma_code'] != null && item.data['ma_code'].toString().trim() != '' 
				  && item.data['ma_code'] == mrec.data['mm_code']){
				  mItem = item;
				  if(item.data['ma_thisqty'] > 0 && mrec.data['mm_oneuseqty'] > 0){
					  max = item.data['ma_thisqty'] * mrec.data['mm_oneuseqty'];
					  max = max.toFixed(length);
					  tqty = Math.min(max, t);
				  }
			  }
		});	
		if(val > tqty) {
			if(!busy)
				showError("本次退料的数量不得超过" + tqty);
			val = tqty;
			record.set('mm_thisqty', val);
		} else if(val < 0){
			if(!busy)
				showError("本次退料的数量不能是负数");
			val = tqty;
		} else if(val == 0 && tqty != 0){
			val = tqty;
		}
		if(record.get('mm_thisqty') != val)
			record.set('mm_thisqty', val);
		return val;
	},
	//鼎智 退料主表
	pm_make_return_thisqty_assgin: function(val, meta, record, x, y){
		var code = record.data['ma_code'], busy = Ext.getCmp('grid').busy;
		record.hasdataChanged=false;
		if(!Ext.isEmpty(record.data['ma_id']) && !Ext.isEmpty(code)) {
			var nQty = record.data['ma_qty'];
			if(!record.maxQty || record.maxQty!=nQty){
				record.maxQty=nQty;
				record.hasdataChanged=true;
			}
			var maxQty = Math.min(record.maxQty, nQty);
			if(val > maxQty) {
				if(!busy && !record.hasdataChanged)
					showError('套数不能超过<' + maxQty + '>,单号:' + code);
				val = maxQty;
				if(record.data['ma_thisqty'] != val) {
					record.set('ma_thisqty', val);
				}
			} else if(val < 0){
				val= 0;
				if(record.data['ma_thisqty'] != val) {
					record.set('ma_thisqty', val);
				}
			}
		}
		/*if(val>0){
			record.data.ma_remainqty = 0;
		}*/
		return val;
	},
	/**
	 * @PM.Make  鼎智 从表 退料
	 * 制造单本次退料数mm_thisqty<=mm_assignqty
	 */
	pm_make_rqty_assgin: function(val, meta, record, x, y, store){
		meta.style = "background:#C6E2FF;";
		if(record.data['mm_onlineqty'] < 0) {// 返修工单退料
			return val;
		}
		var mrec = record;
//		if(record.data.isrep) {
//			//替代料 本次数量 按主料本次数量计算
//			store.each(function(d){
//				if(d.data.mm_id == record.data.mm_id && !d.data.isrep) {
//					mrec = d;return;
//				}
//			});
//		}
		 //判断单位用量的小数位数,本次退料的小数位数与单位用量的相同
		var length = mrec.data['mm_oneuseqty'].toString().split(".")[1]?mrec.data['mm_oneuseqty'].toString().split(".")[1].length:0;
		var busy = Ext.getCmp('editorColumnGridPanel').busy,t;
		t = record.data['mm_assignqty'];
//		t = t.toFixed(length);
		var max = t;
		max = max < 0 ? 0 : max;
		var tqty = max;
		var items = Ext.getCmp('grid').store.data.items;
		if(val > tqty) {
			if(!busy )
				showError("本次退料的数量不得超过" + tqty);
			val = tqty;
		} else if(val < 0){
			if(!busy)
				showError("本次退料的数量不能是负数");
			val = tqty;
		} else if(val == 0 && tqty != 0){
			return val;
		}
		return val;
	},
	/**
	 * @PM.Make
	 * 成套补料,本次补料套数
	 */
	pm_make_add_thisqty: function(val, meta, record, x, y){
		var code = record.data['ma_code'], busy = Ext.getCmp('grid').busy;
		if(!Ext.isEmpty(record.data['ma_id']) && !Ext.isEmpty(code)) {
			var nQty = record.data['ma_qty'] - record.data['ma_haveqty'];
			record.maxQty = record.maxQty || nQty;
			var maxQty = Math.min(record.maxQty, nQty);
			if(val > maxQty) {
				if(!busy)
					showError('本次补料套数不能超过<' + maxQty + '>,单号:' + code);
				val = maxQty;
				if(record.data['ma_thisqty'] != val) {
					record.set('ma_thisqty', val);
				}
			} else if(val < 0) {
				val = 0;
				if(record.data['ma_thisqty'] != val) {
					record.set('ma_thisqty', val);
				}
			}
			var grid = Ext.getCmp('editorColumnGridPanel'),items = grid.store.data.items;
			Ext.each(items, function(item){
				if(item.data['mm_code'] == code){
					var max = item.data['mm_oneuseqty'] * val;
					//判断单位用量的小数位数,本次退料的小数位数与单位用量的相同
					var length = item.data['mm_oneuseqty'].toString().split(".")[1]?item.data['mm_oneuseqty'].toString().split(".")[1].length:0;
					max = max.toFixed(length);
					var t = 0;
					if(item.data.isrep) {
						t = 0;
					} else {
						if(item.data['mm_qty'] == Math.floor(item.data['mm_qty'])){
							t = item.data['mm_scrapqty'] + item.data['mm_returnmqty'] - Math.floor(item.data['mm_balance'])
						           - item.data['mm_addqty'];// - record.data['mm_totaluseqty']
						}else{
							t = item.data['mm_scrapqty'] + item.data['mm_returnmqty'] - item.data['mm_balance']
						           - item.data['mm_addqty'];// - record.data['mm_totaluseqty']
						}		
					}
					max = Math.min(t, max);
					if(item.data['mm_thisqty'] != max){
						item.set('mm_thisqty', max);
					}
				}
			});
		}
		return val;
	},
	/**
	 * @PM.Make
	 * 本次补料数: mm_scrapqty(报废) + mm_returnmqty(制程不良退料数) - mm_balance(备损数) - mm_addqty(补料数)// - mm_totaluseqty(已转领料数量)
	 * 
	 */
	pm_make_thisadd: function(val, meta, record, x, y) {
		meta.tdCls = "x-grid-cell-renderer-bl";
		var t = 0,busy = Ext.getCmp('editorColumnGridPanel').busy;
		var ta = record.data['mm_oneuseqty'].toString().split(".");
		var length = ta[1]?ta[1].length:0;
		if(record.data.isrep) {
			return val;
		} else {
			if(record.data['mm_qty'] == Math.floor(record.data['mm_qty'])){
				t = record.data['mm_scrapqty'] + record.data['mm_returnmqty'] - Math.floor(record.data['mm_balance'])
			           - record.data['mm_addqty'];// - record.data['mm_totaluseqty']
			}else{
				t = record.data['mm_scrapqty'] + record.data['mm_returnmqty'] - record.data['mm_balance']
			           - record.data['mm_addqty'];// - record.data['mm_totaluseqty']
			}	
			//判断单位用量的小数位数,本次退料的小数位数与单位用量的相同
			t = t.toFixed(length);
		}
		var max = t;
		max = max < 0 ? 0 : max;
		var tqty = max;
		if(Ext.getCmp('grid')){
			var items = Ext.getCmp('grid').store.data.items;
			Ext.each(items, function(item){
				if(item.data['ma_code'] != null && item.data['ma_code'].toString().trim() != '' 
					&& item.data['ma_code'] == record.data['mm_code']){
					mItem = item;
					if(item.data['ma_thisqty'] > 0 && record.data['mm_oneuseqty'] > 0){
						max = item.data['ma_thisqty'] * record.data['mm_oneuseqty'];
						max = max.toFixed(length);
						tqty = Math.min(max, t);
					}
				}
			});
		}		
		if(val > tqty) {
			if(!busy)
				showError("本次补料数不得超过" + tqty);
			val = tqty;
			record.set('mm_thisqty', val);
		} else if(val < 0){
			if(!busy)
				showError("本次补料数不能是负数");
			val = tqty;
			record.set('mm_thisqty', val);
		} else if(val == 0 && tqty != 0){
			val = tqty;
			record.set('mm_thisqty', val);
		}
		return val;
	},
	/**
	 * 本次补料 添加锁定库存数 欧盛
	 */
	pm_make_thisaddosdb: function(val, meta, record, x, y) {
		meta.tdCls = "x-grid-cell-renderer-bl";
		var t = 0,busy = Ext.getCmp('editorColumnGridPanel').busy;
		var ta = record.data['mm_oneuseqty'].toString().split(".");
		var length = ta[1]?ta[1].length:0;
		if(record.data.isrep) {
			return val;
		} else {
			if(record.data['mm_qty'] == Math.floor(record.data['mm_qty'])){
				t = record.data['mm_scrapqty'] + record.data['mm_returnmqty'] - Math.floor(record.data['mm_balance'])
			           - record.data['mm_addqty'];// - record.data['mm_totaluseqty']
			}else{
				t = record.data['mm_scrapqty'] + record.data['mm_returnmqty'] - record.data['mm_balance']
			           - record.data['mm_addqty'];// - record.data['mm_totaluseqty']
			}	
			//判断单位用量的小数位数,本次退料的小数位数与单位用量的相同
			t = t.toFixed(length);
		}
		var max = t;
		max = max < 0 ? 0 : max;
		var tqty = max;
		var items = Ext.getCmp('grid').store.data.items;
		Ext.each(items, function(item){
			if(item.data['ma_code'] != null && item.data['ma_code'].toString().trim() != '' 
				&& item.data['ma_code'] == record.data['mm_code']){
				mItem = item;
				if(item.data['ma_thisqty'] > 0 && record.data['mm_oneuseqty'] > 0){
					max = item.data['ma_thisqty'] * record.data['mm_oneuseqty'];
					max = max.toFixed(length);
					tqty = Math.min(max, t);
				}
			}
		});
		tqty = Math.min(tqty,(record.data["mm_assignqty"]== undefined?0:record.data["mm_assignqty"]));
		if(val > tqty) {
			if(!busy)
				showError("本次补料数不得超过" + tqty);
			val = tqty;
			record.set('mm_thisqty', val);
		} else if(val < 0){
			if(!busy)
				showError("本次补料数不能是负数");
			val = tqty;
			record.set('mm_thisqty', val);
		} else if(val == 0 && tqty != 0){
			val = tqty;
			record.set('mm_thisqty', val);
		}else{
			if(val>(record.data["mm_assignqty"]== undefined?0:record.data["mm_assignqty"])){
				showError("本次补料数不能大于锁定库存数");
				Ext.defer(function(){
					record.set('mm_thisqty', val);
				},10);
			}
		}
		return val;
	},
	/**
	 * @PM.Make
	 * ProductAll!Query  MRP仓剩余可用=pr_mrponhand-pr_commited
	 */
	pm_ProductAll_onhandlack: function(val, meta, record, x, y){
		var t = 0 ; 
		t = record.data['v_po_mrponhand'] - record.data['v_mrpsaqty'] - record.data['v_forecastqty'] - record.data['v_mrpmmqty'] ;
		var field = this.columns[y].dataIndex;
		return t;
		//record.set(field, t);
	},
	/**
	 * @PM.Make
	 * ProductAll!Query 收料仓剩余可用=pr_mrponhand+pr_reconhand-pr_commited
	 */
	pm_ProductAll_reconhandlack: function(val, meta, record, x, y){
		var t = 0 ;
		t = record.data['v_po_mrponhand'] + record.data['v_reconhand'] - record.data['v_mrpsaqty'] - record.data['v_forecastqty'] - record.data['v_mrpmmqty'] ;
		var field = this.columns[y].dataIndex;
		return t;
	},
	/**
	 * @PM.Make
	 * ProductAll!Query  MRP在途剩余可用=pr_mrponhand+pr_onorder-pr_commited
	 */
	pm_ProductAll_onorderlack: function(val, meta, record, x, y){
		var t = 0 ;
		t = record.data['v_po_mrponhand'] + record.data['v_mrppoqty'] + record.data['v_mrpmaqty'] - record.data['v_forecastqty'] - record.data['v_mrpsaqty']- record.data['v_mrpmmqty'] ;
		var field = this.columns[y].dataIndex;
		return t;
	},
	/**
	 * @PM.Make
	 * ProductAll!Query  MRP请购剩余可用=pr_mrponhand+pr_onorder+pr_arkqty-pr_commited
	 */
	pm_ProductAll_arklack: function(val, meta, record, x, y){
		var t = 0;
		t = record.data['v_po_mrponhand'] + record.data['v_mrppoqty'] + record.data['v_mrpmaqty'] + record.data['v_arkqty'] - record.data['v_mrpsaqty'] - record.data['v_forecastqty'] - record.data['v_mrpmmqty'] ;
		var field = this.columns[y].dataIndex;
		return t;
	},
	/**
	 *@PM.MakePlan 生产计划维护 
	 * */
	pm_make_presMakePlan:function(val,meta,record,rol){
		if(val)
			return '<a href="javascript:showWin('+ '\''+rol+'\''+');">' + val + '</a>';
		else return null;
	},
	/**
	 * 颜色列
	 * @expression bgcolor
	 */
	bgcolor: function(val, meta, record, x, y){
		return "<div style='background:#" + val + " !important'>" + val + "</div>";
	},
	/**
	 * 颜色列
	 * @expression bgcolor2
	 */
	bgcolor2: function(val, meta, record, x, y){
		return '<div style="background:#' + val + '">&nbsp;</div>';
	},
	/**
	 * 超链接列
	 */
	href: function(val, meta, record, x, y){
		return '<a href="javascript:void(0);">' + val + '</a>';
	},
	/**
	 * 年和月
	 */
	yearmonth: function(val, meta, record, x, y){
		meta.style = "background:#C6E2FF;";
		if(val == null || val == ''){
			var date = new Date();
			var year = date.getFullYear();
			var month = date.getMonth() + 1;
			month = month < 10 ? '0' + month : month;
			val = '' + year + month;
		}
		var field = this.columns[y].dataIndex;
		if(record.data[field] != val){
			record.set(field, val);
		}
		return val;
	},
	/**
	 * ??
	 */
	date: function(val, meta, record){
		meta.style = "background:#C6E2FF;";
		if(val){
			return '<span style="color:blue;padding-left:2px">' + Ext.Date.toString(val); + '</span>';
		} else 
			return '<span style="color:blue;padding-left:2px"></span>';
	},
	/**
	 * 网络寻呼-状态prd_status
	 * -1未读 1已读 0保留
	 */
	pagingReleaseStatus: function(val, meta, record){
		var d = Number(val);
		var str = '';
		switch (d){
		case 0:
			str = '<span style="color:#20B7B9;padding-left:2px">保留</span>';
			break;
		case 1:
			str = '<img src="' + basePath + 'resource/images/renderer/remind.png">' + 
			'<span style="color:blue;padding-left:2px">已阅</span>';
			break;
		case -1:
			str = '<img src="' + basePath + 'resource/images/renderer/remind2.png">' + 
			'<span style="color:red;padding-left:2px">未阅</span>';
			break;
		}
		return str;
	},
	/**
	 * @OA
	 * 不通过监听itemmousedown来查看明细，
	 * 而是点击button
	 */
	detailbutton: function(msg, meta, record){
		/**
		 * 寻呼内容列表显示时
		 * 将表情代码转化成图片。
		 * 插入的图片和附件代码不转化
		 */
		if(msg){
			msg = msg.toString();
			var faces = msg.match(/&f\d+;/g);
			Ext.each(faces, function(f){//表情
				msg = msg.replace(f, '<img src="' + basePath + 'resource/images/face/' + f.substr(2).replace(';', '') + '.gif">');
			});
		}
		return (msg || ' ') + "<input type='button' value='查看详细' name='detailbutton' style='float:right;color:gray;font-size:13px;cursor:pointer;height:25px;' onClick='Ext.getCmp(\"grid\").onGridItemClick();'/>";
	},
	opendetail: function(val, meta, record) {
		if(!Ext.isEmpty(val)){
			var sd_id = record.get('sd_id');
			return val + "<input type='button' value='出货排程' name='detailbutton' " + 
			"style='float:right;color:gray;font-size:13px;cursor:pointer;height:25px;' onClick='window.open(\"" + basePath + 
			"jsps/scm/sale/saleDetail.jsp?formCondition=sd_id="+sd_id+"&gridCondition=sdd_sdid="+sd_id+"\", \"测试\", \"width=800,height=600,top=30,left=200\")'/>";
		}
	},
	/**
	 * 不直接显示附件的ID字符串，转化显示附件数
	 */
	attachcolumn: function(val, meta, record){
		if(val != null && val != ''){
			return val.split(',').length + ' 个';        		
		} else {
			return '无';
		}
	},
	/**
	 * 对任务完成率不同阶段进行图片标识
	 */
	percentdone: function(val, meta, record){
		val == null || 0;
		if(val < 30)
			return '<img src="' + basePath + 'resource/images/renderer/remind2.png">'+'<span style="color:#436EEE;padding-left:2px">' + val + '</span>';
		else if(val > 30 && val < 50) 
			return '<img src="' + basePath + 'resource/images/renderer/remind.png">'+'<span style="color:#5F9EA0;padding-left:2px">' + val + '</span>';
		else if(val == 100){
			return '<img src="' + basePath + 'resource/images/renderer/award1.png">'+'<span style="color:blue;padding-left:2px">' + val + '</span>';
		}else if(val > 80){
			return '<img src="' + basePath + 'resource/images/renderer/award2.png">'+'<span style="color:green;padding-left:2px">' + val + '</span>';
		}
		else 
			return val;
	},
	/**
	 * SCM 请购转采购替代料 
	 * */
	ap_isrep: function(val, meta, record, x, y){
		if(record.data.ad_ifrep==-1) {
			meta.tdCls = "x-grid-cell-renderer-cl";
			return '<img src="' + basePath + 'resource/images/taskdescription.png">' + val;
		}
		return val;
	},
	isDateModel_type: function(val){
		if(val =='zhou'){
			return '周';
		} else if(val == 'yue'){
			return '月';
		} else {
			return '未知';
		}
	},
	string_substring: function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		if(!me || !me.args){
			return val;
		} else {
			var i=me.args['string_substring'][field];
			return val.substring(Number(i[0]),Number(i[1]));
		}

	},
	/**
	 * @MA
	 */
	logicDesc_type: function(val){
		if (val == 1) {
			return '主算法';
		} else if(val == 0){
			return '一般算法';
		} else {
			return '系统算法';
		}
	},
	/**
	 * 通用方法
	 * 获取本地常用属性
	 * em_name,em_code,em_uu,en_uu...
	 * 以及获取本地时间
	 * @expression getLocal(em_code),getLocal(Y-m-d),getLocal(time)
	 */
	getLocal: function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		if(!me || !me.args){
			grid = grid.ownerCt, me = grid.RenderUtil;
			if(!me || !me.args){
				return val;
			}
		}
		var arg = me.args.getLocal[field][0],v = val;
		if(contains('Y-m-d H:i:s', arg)) {
			v = Ext.Date.format(new Date(), arg);
		} else if('time' == arg){
			v = Ext.Date.format(new Date(), 'Y-m-d H:i:s');
		} else if('date' == arg){
			v = Ext.Date.format(new Date(), 'Y-m-d');
		} else {
			v = window[arg];
		}
		if(v && Ext.isEmpty(val)) {
			val = v;
			record.set(field, v);
		}
		return val;
	},
	/**
	 * @MA
	 * 逻辑顺序 0-before,1-after
	 */
	logicturn: function(val){
		switch (Number(val)){
		case 0 :
			val = 'before';break;
		case 1 :
			val = 'after';break;
		}
		return val;
	},
	/**
	 * @MA
	 * form配置界面,将从datadictonary得到的ddd_fieldtype转化为form能识别的fd_type
	 */
	form_type: function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,column = grid.columns[y],e,f, _v = val ? val.toLowerCase() : '', len = 20;
		if(/^(varchar2)/.test(_v)) {
			val = 'S';
			len = Number(_v.substring(_v.indexOf('(') + 1, _v.indexOf(')')));
		} else if (/^(int)/.test(_v)) {
			val = 'N';
		} else if (/^(number)/.test(_v)) {
			val = 'N';
		} else if (/^(float)/.test(_v)) {
			val = 'N';
		} else if (/^(smallint)/.test(_v)) {
			val = 'N';
		} else if ('date' == _v) {
			val = 'D';
		} else if ('timestamp' == _v) {
			val = 'DT';
		}
		if (record.get('fd_type') != val) {
			record.set('fd_type', val);
			record.set('fd_fieldlength', len);
		}
		if ((e = (column.editor || column.filter)) != null && e.store) {
			var s = null,dd = e.store.data;
			s = Ext.Array.filter(dd, function(d, index){
				return d.value == val;
			});
			if(s && s.length > 0) {
				return s[0].display;
			}
		} else if ((f = column.field) != null) {
			return f.rawValue;
		}
		return val;
	},
	/**
	 * @FA.VoucherStyle
	 */
	fa_vdclass: function(val, meta, record, x, y, store, view) {
		if(!Ext.isEmpty(val)) {
			var count = 0;
			store.each(function(d){
				if(d.get('vd_class') == val) {
					count++;
				}
			});
			var v = record.get('vd_detno');
			if(v <= 0 || v > count) {
				record.set('vd_detno', count);
			} 
		}
		return val;
	} ,
	/**
	 * @FA.VoucherStyle
	 */
	fa_custmonthcys_batch: function(val, meta, record, x, y, store, view) {

		var cm_endamount = record.data['cm_endamount'];	//应收金额
		var cm_prepayend = record.data['cm_prepayend'];	//预收金额

		if(Ext.isNumber(Number(cm_endamount))&&Ext.isNumber(Number(cm_prepayend))){
			if(cm_endamount == '' && cm_prepayend == ''){
				return '';
			}

			return Number(cm_endamount) > Number(cm_prepayend) ? Ext.util.Format.number(cm_endamount, '0,000.00')  :Ext.util.Format.number(cm_prepayend,'0,000.00') ;
		} else {
			return '';
		}




	} ,
	/**
	 * @流程等待时间
	 */
	WaitTime:function(val, meta, record){
		if(record){
			var launchTime=record.data.jp_launchTime;	
			return parseInt((new Date().getTime()-new Date(launchTime).getTime())/60000);
		}
		else return null;
	},
	/**
	 * @FA.Voucher
	 * VoucherCreate
	 * 打开凭证
	 */
	openvoucher: function(val, meta, record) {
		if(!Ext.isEmpty(val)) {
			if(!window.__fn) {
				var fn = function(vo_code) {
					Ext.Ajax.request({
						url: basePath + 'common/getFieldData.action',
						params: {
							caller: 'Voucher',
							field: 'vo_id',
							condition: 'vo_code=\'' + vo_code + '\''
						},
						callback: function(opt, s, r) {
							if(s) {
								var rs = Ext.decode(r.responseText);
								if(rs.data != null && rs.data > 0) {
									openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS' + rs.data + 
											'&gridCondition=vd_voidIS' + rs.data);
								}
							}
						}
					});
				};
				window.__fn = fn; 
			}
			val = '<a href="javascript:window.__fn(\'' + val + '\');">' + val + '</a>';
		}
		return val;
	},
	prod_disable : function(v, m, r) {
		var s = r.get('pr_status');
		m.style = '';
		if('已删除' == s || '已禁用' == s) {
			m.style = 'color:red;';
		}
		return v;
	},
	/**
	 * @PM
	 * 制造单批量转FQC前，判断根据物料大类，批号需要手工录入(国扬)
	 **/
	turnfqc_requirebatch : function(v, m, r, x, y, store, view) {
		var kinds = ['CWDM模块','PCBA板','PCB板','光开关','结构件','模拟光模块','模拟光器件','数字光模块','数字光器件','数字器件材料','无源半成品','无源器件材料'];
		if(kinds.indexOf(v) > -1 && view.ownerCt.selModel.isSelected(r) && Ext.isEmpty(r.get('ma_contractcode'))) {
			showError('需要填写批号，工单:' + r.get('ma_code'));
		}
		return v;
	},
	/**
	 * @Scm.Application
	 * 请购单差异天数计算(国扬)
	 **/
	application_differdays : function(val, meta, record){
		var v = record.get('ad_delivery');
		if(v != null) {
			return Math.round((v-Ext.getCmp('ap_date').getValue())/86400000)-(record.get('ad_leadtime'));
		}
		return 0;
	},
	/**
	 * @Pm.MakeScrap
	 * 报废单是否超备损数报废
	 **/
	ifoverqtyScrap : function(val, meta, record){
		var v1 = record.get('md_allscrapqty');
		var v2 = record.get('mm_balanceqty');
		var v;
		if(v1>0 && v1>v2) {
			v='是';
		}else  
			v= '否';
		return v;
	},
	/**
	 * @Scm。ProdInOut
	 * 生产退料单，是否超损耗退料
	 **/
	ifoverqtyReturn : function(val, meta, record){
		var v1 = record.get('pd_orderqty');
		var v2 = record.get('mm_balanceqty');
		var v;
		if(v1>0 && v1>v2 && record.get('pd_description')=="制程不良") {
			v='是';
		}else  
			v= '否';
		return v;
	},

	/**
	 * @Pm。
	 * 增加物料的关联信息查看render(鼠标停在某个物料上时，显示一个tip提示信息，
	 * 包含物料的MRP库存，不良品库存，采购待检数，PO在途数，请购在途数，工单未发料数，工单为完工数)
	 **/
	ProductHref : function(val, meta, record,x,y,store, view){		
		 var field = this.columns[y].dataIndex;
         var me = this.RenderUtil;
         var grid = view.ownerCt;        
     	 document.getElementById(view.el.id).addEventListener("mouseover", function(e){
     	    callValue = e.target.innerHTML;
     	    if(typeof(me.args.ProductHref) != undefined && me.args.ProductHref != null){//带有参数，分别指参数列
     	       var rfield = me.args.ProductHref[field];  
     	       for(var i=0;i<rfield.length;i++){//当前所指的单元格名称等于参数
            	if(record.data[rfield[i]] == callValue){
            		code = callValue;
            		getData();
            	}
     	       }
     	    }else{//不带参数指的是该列
     	    	if(record.data[field] == callValue){
     	    		code = callValue;
     	    		getData();
     	    	}
     	    }
     	});
     	function getData(){
	        if(!view.tip || (view.tip && view.tip.hidden)) {
	        	if(view.tip && view.tip.hidden){
	        		view.tip.destroy();
	        	}
				view.tip = Ext.create('Ext.tip.ToolTip', {
					target: view.el,
					delegate: view.itemSelector,
					trackMouse: true,
					renderTo: Ext.getBody(),	
					maxWidth :580,
					hidden : false,
					listeners: {								          
						beforeshow: function updateTipBody(tip) {
							if(callValue == code){
								Ext.Ajax.request({ 							             							    	      
									url: basePath + 'pm/product/getProductCount.action',
									params: {
										codes: code
									},
									callback: function (opt, s, r) {							    		         	
										if(s) {
											var rs = Ext.decode(r.responseText);							    				        
											if(rs.data) {	
												tip.down('grid').setTitle(code);
												tip.down('grid').store.loadData(rs.data);
											}
										} 
									}
								});
							}else{//不显示tip
								return false;
							}
						}
					},
					items: [{
						xtype: 'grid',
						width: 580,								        	
						columns: [{
							text: 'MRP库存',
							cls: 'x-grid-header-1',
							dataIndex:'PO_MRPONHAND',
							width: 80
						},{
							text: '不良品库存',
							cls: 'x-grid-header-1',
							dataIndex: 'PO_DEFECTONHAND',
							width: 80
						},{
							text: '采购待检数',
							cls: 'x-grid-header-1',
							xtype: 'numbercolumn',
							align: 'right',
							dataIndex: 'RECONHAND',
							width: 80
						},{
							text: 'PO在途数',
							cls: 'x-grid-header-1',
							xtype: 'numbercolumn',
							align: 'right',
							dataIndex: 'POQTY',
							width: 80
						},{
							text: '请购在途数',
							cls: 'x-grid-header-1',
							xtype: 'numbercolumn',
							align: 'right',
							dataIndex: 'ARKQTY',
							width: 80
						},{
							text: '工单未发料数',
							cls: 'x-grid-header-1',
							xtype: 'numbercolumn',
							align: 'right',
							dataIndex: 'MMQTY',
							width: 80
						},{
							text: '工单未完工数',
							cls: 'x-grid-header-1',
							xtype: 'numbercolumn',
							align: 'left',
							dataIndex: 'MAQTY',
							width: 100
						}],
						columnLines: true,
						title: '物料分仓库存',
						store: new Ext.data.Store({
							fields: ['PO_MRPONHAND', 'PO_DEFECTONHAND', 'RECONHAND','POQTY','ARKQTY','MMQTY','MAQTY'],
							data: [{}]
						})
					}]
				});
	        }
     	}
		var field = this.columns[y].dataIndex;
		return record.data[field];
	},
	/**
	 * @Pm。
	 * 增加物料的关联信息查看render(鼠标停在某个物料上时，显示一个tip提示信息，
	 * 包含物料的仓库编号，仓库名称，库存),类似于erp.view.core.plugin.ProdOnhand,委外成套发料界面中的显示效果
	 * 类似配置方式 ProdOnhand:pd_prodcode,或者直接配置为ProdOnhand，如果没有添加字段则默认选取的字段就是该行字段名
	 **/
	ProdOnhand : function(val, meta, record,x,y,store, view){	   
		 var field = this.columns[y].dataIndex;
         var me = this.RenderUtil;
         var grid = view.ownerCt;
         var rfield = field;
         if(me != null && me.args.ProdOnhand != null){
         	rfield = me.args.ProdOnhand[field][0]; 
         }
         var code = record.data[rfield];
         if(code) { 
			if(!view.tip) {				
				view.tip = Ext.create('Ext.tip.ToolTip', {
					target: view.el,
					delegate: view.itemSelector,
					trackMouse: true,
					renderTo: Ext.getBody(),	
					maxWidth :830,
					listeners: {								          
						beforeshow: function updateTipBody(tip) {
							var rowindex = tip.triggerElement.rowIndex;
							var code = Ext.getCmp(grid.id).getStore().getAt(rowindex-1).get(rfield);
							Ext.Ajax.request({ 							             							    	      
								url : basePath + 'scm/product/getProductwh.action',
								params : {
									codes : "'"+code+"'"
								},
								callback: function (opt, s, r) {							    		         	
									if(s) {
										var rs = Ext.decode(r.responseText);							    				        
										if(rs.data) {
											tip.update('');			
											tip.down('grid').setTitle(code);
											tip.down('grid').show();
											tip.down('grid').store.loadData(rs.data);	
										}else{
											tip.down('grid').hide();											
											tip.update(code+'<br/> 库存数量为0');
										}
									} 
								}
							});
						}
					},
					items : [{
						xtype : 'grid',
						width : 1300,
						columns : [ {
							text : '仓库编号',
							cls : 'x-grid-header-1',
							dataIndex : 'PW_WHCODE',
							width : 80
						}, {
							text : '仓库名称',
							cls : 'x-grid-header-1',
							dataIndex : 'WH_DESCRIPTION',
							width : 120
						}, {
							text : '库存',
							cls : 'x-grid-header-1',
							xtype : 'numbercolumn',
							align : 'right',
							dataIndex : 'PW_ONHAND',
							width : 90
						}, {
							text : '不良品仓库存数',
							cls : 'x-grid-header-1',
							xtype : 'numbercolumn',
							align : 'right',
							dataIndex : 'PO_DEFECTQTY',
							width : 130
						}, {
							text : 'MRP仓库存数',
							cls : 'x-grid-header-1',
							xtype : 'numbercolumn',
							align : 'right',
							dataIndex : 'PO_MRPONHAND',
							width : 130
						}, {
							text : '良品仓库存数',
							cls : 'x-grid-header-1',
							xtype : 'numbercolumn',
							align : 'right',
							dataIndex : 'LP_QTY',
							width : 130
						}, {
							text : '未过账出库数',
							cls : 'x-grid-header-1',
							xtype : 'numbercolumn',
							align : 'right',
							dataIndex : 'PD_QTY',
							width : 130
						} ],
						columnLines : true,
						title : '物料分仓库存',
						store : new Ext.data.Store({
							fields : [ 'PW_WHCODE', 'WH_DESCRIPTION', 'PW_ONHAND','PO_DEFECTQTY','PO_MRPONHAND','LP_QTY','PD_QTY' ],
							data : [ {} ]
						})
					} ]
				});				
			}
         }		
		return record.data[field];
	},
	
	/**
	 * 可进行配置的tooltip,显示的是配置caller的grid数据 maz 
	 */
	ProdOnhand_Config : function(val, meta, record,x,y,store, view){	   
		 var field = this.columns[y].dataIndex;
         var me = this.RenderUtil;
         var grid = view.ownerCt;
         var rfield = field;
         var caller = '';
         var cond = '';
         var width = '';
         if(me != null && me.args.ProdOnhand_Config != null){
         	rfield = me.args.ProdOnhand_Config[field][0].split('|')[0]; 
         	caller = me.args.ProdOnhand_Config[field][0].split('|')[1].split('=')[0];
         	cond = me.args.ProdOnhand_Config[field][0].split('|')[1].split('=')[1].split(',')[0];
         	width = me.args.ProdOnhand_Config[field][0].split('|')[1].split('=')[1].split(',')[1];
         }
         var code = record.data[rfield];
         var condition = cond+'='+ '\'' + code + '\'';
         if(code) { 
			if(!view.tip) {				
				view.tip = Ext.create('Ext.tip.ToolTip', {
					target: view.el,
					delegate: view.itemSelector,
					trackMouse: true,
					renderTo: Ext.getBody(),	
					maxWidth :830,
					listeners: {								          
						beforeshow: function updateTipBody(tip) {
							var rowindex = tip.triggerElement.rowIndex;
							code = Ext.getCmp(grid.id).getStore().getAt(rowindex-1).get(rfield);
					        condition = cond+'='+ '\'' + code + '\'';
					        var grid5 = Ext.getCmp(me.id+'_grid');
					        grid5.setLoading(true);//loading...
					        var param = {caller:caller,condition:condition};
					        if(!param._config) param._config=getUrlParam('_config');
					        Ext.Ajax.request({//拿到grid的columns
					        	url : basePath + "common/loadNewGridStore.action",
					        	params: param,
					        	method : 'post',
					        	callback : function(options,success,response){
					        		grid5.setLoading(false);
					        		var res = new Ext.decode(response.responseText);
					        		if(res.exceptionInfo){
					        			showError(res.exceptionInfo);return;
					        		}
					        		var data = res.data;
					        		if(!data || data.length == 0){
					        			grid5.hide();											
										tip.update(code+'<br/> 库存数量为0');
					        		} else {
					        			tip.update('');
					        			grid5.setTitle(code);
				        				grid5.store.loadData(data);
				        				grid5.show();
					        		}
					        	}
					        });
						}
					},
					items : [Ext.create('erp.view.core.grid.Panel5', {
						id: me.id+'_grid',
						anchor: '100% 100%',
						caller: caller,
						condition: condition,
						layout : 'fit',
						width:width,
						bbar: null
					})]
				});		
			}
         }		
		return record.data[field];
	},
	/**
	 * MRP需求界面增加类似成套发料的ProdOnhand 这个plugin一样
	 */
	ProdOnhandMRP: function(val, meta, record,x,y,store, view){	   
		var field = this.columns[y].dataIndex;
		var render_field = this.columns[y].dataIndex;
        var me = this.RenderUtil;
        var grid = view.ownerCt;
        var rfield = field;
        var master,current;
        if(me != null && me.args.ProdOnhandMRP != null){
        	rfield = me.args.ProdOnhandMRP[field][0];//获取实际字段
        	master = me.args.ProdOnhandMRP[field][1]; //获取账套
//        	current = me.args.ProdOnhandMRP[field][2]; //只有在该字段显示Tip
        }
        var code = record.data[rfield];
        if(code) { 
			if(!view.tip) {				
				view.tip = Ext.create('Ext.tip.ToolTip', {
					target: view.el,
//					delegate: '.x-grid-cell-inner',
					delegate: view.cellSelector,
					trackMouse: true,
					renderTo: Ext.getBody(),	
					maxWidth :830,
					listeners: {								          
						beforeshow: function updateTipBody(tip) {
							var cell = tip.triggerElement;
		                    var dataIndex = view.getHeaderByCell(cell).dataIndex;
		                    var rec = view.getRecord(cell.parentNode);
							var code = rec.get(rfield);
							var allMaster = ["CJMY_POHAND","BFKM_POHAND","XKN_POHAND","HQSM_POHAND","BJH_POHAND","LMMY_POHAND"];
		                    if(allMaster.indexOf(dataIndex) < 0){
		                    	return false;
		                    }else{
		                    	master = dataIndex.split('_')[0];
		                    }
		                    Ext.Ajax.request({ 							             							    	      
		                    	url : basePath + 'pm/product/getProductwhMaster.action',
		                    	params : {
		                    		master: master,
		                    		codes : code
		                    	},
		                    	async:false,
		                    	callback: function (opt, s, r) {
		                    		if(s) {
		                    			var rs = Ext.decode(r.responseText);							    				        
		                    			if(rs.data) {
		                    				tip.update('');			
		                    				tip.down('grid').setTitle("账套："+rs.data[0]['COMPANYNAME']+",物料编号："+code);
		                    				tip.down('grid').show();
		                    				tip.down('grid').store.loadData(rs.data);	
		                    			}else{
		                    				tip.down('grid').hide();											
		                    				tip.update(code+'<br/> 库存数量为0');
		                    			}
		                    		} 
		                    	}
		                    });
						}
					},
					items : [{
						xtype : 'grid',
						width : 420,
						columns : [ {
							text : '仓库编号',
							cls : 'x-grid-header-1',
							dataIndex : 'V_PW_WHCODE',
							width : 80
						}, {
							text : '仓库名称',
							cls : 'x-grid-header-1',
							dataIndex : 'V_WH_DESCRIPTION',
							width : 120
						}, {
							text : '库存',
							cls : 'x-grid-header-1',
							xtype : 'numbercolumn',
							align : 'right',
							dataIndex : 'V_PW_ONHAND',
							width : 90
						},{
							text : '账套名',
							cls : 'x-grid-header-1',
							align : 'right',
							dataIndex : 'COMPANYNAME',
							width : 90
						}],
						columnLines : true,
						title : code,
						store : new Ext.data.Store({
							fields : [ 'V_PW_WHCODE', 'V_WH_DESCRIPTION', 'V_PW_ONHAND','COMPANYNAME' ],
							data : [ {} ]
						})
					} ]
				});				
			}
        }		
		return record.data[field];
	},
	/**
	 * @SCM.Purchase.VerifyApplyChange
	 * 收料变更单新数量
	 */
	scm_verify_change_qty: function(val, meta, record){
		meta.style = "background:#C6E2FF;";
		var pdq = record.data['pd_qty'], oldqty = record.data['vcd_oldqty'],
		yqty = record.data['pd_yqty'];
		if(pdq != null && pdq > 0) {
			if(yqty + (val-oldqty) > pdq) {
				val = pdq-yqty+oldqty;
				showError('请不要超过采购数<' + pdq + '>!');
			}
		}
		if(record.get('vcd_newqty') != val) {
			record.set('vcd_newqty', val);
		}
		return val;
	},
	/**
	 * @PM ProdInOut!Partition!Deal
	 * 拆件批量入库 （入库数）
	 */
	pm_io_thisqty: function(val, meta, record, x, y, store, view){
		meta.tdCls = "x-grid-cell-renderer-bl";
		var grid = view.ownerCt, me = grid.RenderUtil,column = grid.columns[y]	;
		var gqty = record.data['mm_gqty'];
		var yqty = record.data['mm_yqty'];		
		if(gqty != null && gqty > 0 ) {
			var maxValue = gqty-yqty;		
			if(record.dirty){
				var thisqty = val  ;	
				if(column.editor || (column.getEditor && column.getEditor())) {//在允许编辑的情况下，修改值不能大于maxValue
					if (thisqty > maxValue){
						val = maxValue; 
						showError('请不要输入超过最大数量<' + maxValue + '>的值!');					
					}
				}
			}else{
				val = maxValue;
			}	
			if(record.data['mm_thisqty'] != val){
				record.set('mm_thisqty', val);
			}
			return val;
		}
	},
	/**
	 * @Fa
	 * 应付批量开票
	 * */
	FA_THISVOQTY: function(val, meta, record, x, y, store, view){
		var maxValue=record.get('pd_showqty')-record.get('pd_showinvoqty');
		var grid = view.ownerCt,column = grid.columns[y],field = column.dataIndex;
		if(maxValue>0 && val<0){
			val=maxValue;
			showError('开票数量不能小于0!');
			record.set(field,val);
		
		}		
		else if(maxValue<0 && val>0){
			val=maxValue;
			showError('开票数量不能大于0!');
			record.set(field,val);
			
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		else
			val = val.toFixed(4);
		return val;
	},
	FA_BILLOUTTHISVOQTY: function(val, meta, record, x, y, store, view){
		var maxValue=record.get('abd_qty')-record.get('abd_yqty');
		var grid = view.ownerCt,column = grid.columns[y],field = column.dataIndex;
		if(maxValue>0 && val<0){		
			val=maxValue;
			showError('开票数量不能小于0!');
			record.set(field,val);
		}		
		else if(maxValue<0 && val>0){		
			val=maxValue;
			showError('开票数量不能大于0!');
			record.set(field,val);
		} else if(!val && !!maxValue) {
			val = maxValue;
			record.set(field,val);
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		else
			val = val.toFixed(4);
		return val;
	},
	/**
	 * @Fa
	 * 对账单转发票
	 * */
	FA_CHECK_THISVOQTY: function(val, meta, record, x, y, store, view){
		var maxValue=record.get('ad_qty')-record.get('ad_yqty');
		var grid = view.ownerCt,column = grid.columns[y],field = column.dataIndex;
		if(maxValue>0 && val<0){
			val=maxValue;
			showError('开票数量不能小于0!');
			record.set(field,val);
		
		} else if(maxValue<0 && val>0){
			val=maxValue;
			showError('开票数量不能大于0!');
			record.set(field,val);
			
		} else if(!val && maxValue) {
			val = maxValue;
			record.set(field,val);
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		else
			val = val.toFixed(4);
		return val;
	},
	/**
	 * @Fa
	 * 对账单转回款通知
	 * */
	FA_CHECK_THISNTQTY: function(val, meta, record, x, y, store, view){
		var ad_qty = record.get('ad_qty')==null ? 0 : record.get('ad_qty');
		var ad_zqty = record.get('ad_zqty')==null ? 0 : record.get('ad_zqty');
		var maxValue = ad_qty-ad_zqty;
		var grid = view.ownerCt,column = grid.columns[y],field = column.dataIndex;
		if(maxValue>0 && val<0){
			val=maxValue;
			showError('开票数量不能小于0!');
			record.set(field,val);
		
		} else if(maxValue<0 && val>0){
			val=maxValue;
			showError('开票数量不能大于0!');
			record.set(field,val);
			
		} else if(!val) {
			val = maxValue;
			record.set(field,val);
		}
		if(column.format)
			val = Ext.util.Format.number(val, column.format);
		else
			val = val.toFixed(4);
		return val;
	},
	BatchRemind: function(val, meta, record){
		if(record.get('ba_validtime') && Number(record.get('ba_remain'))>0 && new Date()>record.get('ba_validtime'))
		return '<span style="color:red;padding-left:2px">' + val + '</span>';
		else return val;
	},
	SMTMonitor :function(val, meta, record, x, y, store, view){
		var me = this;	
		if(Ext.isDefined(me.unitTime)){
			if(Number(me.unitTime)*Number(record.get('msl_remainqty')) < Number(me.warningTime)*60){	
				me.getView().getRowClass = function(record, rowIndex, rowParams, store) {		            
		               return 'renderColor';		            
		        };		        	
				var el = Ext.get('audio-error').dom;
				el.play();		        
		    }
		}
		return val;
	},	
	/**	  
	 * 明细行附件上传下载
	 * */
	detailAttach:function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		var filedAttach = '1detailAttach';
		if(me != null && me.args.detailAttach != null){
			filedAttach = "1"+me.args.detailAttach[field][0]+"detailAttach";
		}
		if(record&&record.data[filedAttach]!=null&&record.data[filedAttach]!=""){		
			var attach=record.data[filedAttach];
			var grid = view.ownerCt,column = grid.columns[y],field = column.dataIndex;
				if(record.data[field] != attach){
				record.set(field,attach);
			}
			/** 文件名存在分号的，使用数组最后一个当文件id */
			var arr = attach.split(";");
			if(arr.length>2){
				return '<a href="' + basePath + 'common/downloadbyId.action?id='+arr[arr.length-1]+'" style="text-decoration:none""><span style="color:green;padding-left:2px;">' + attach.split(";")[0] + '</span>'+'<img src="' + basePath + 'resource/images/icon/download.png" ></a>';
			}
			return '<a href="' + basePath + 'common/downloadbyId.action?id='+attach.split(";")[1]+'" style="text-decoration:none""><span style="color:green;padding-left:2px;">' + attach.split(";")[0] + '</span>'+'<img src="' + basePath + 'resource/images/icon/download.png" ></a>';
		}else if(record&&val!=null&&val!=""){
			/** 文件名存在分号的，使用数组最后一个当文件id */
			var arr =  val.split(";");
			if(arr.length>2){
				return 	'<a href="' + basePath + 'common/downloadbyId.action?id='+arr[arr.length-1]+'" style="text-decoration:none""><span style="color:green;padding-left:2px;">' + val.split(";")[0] + '</span>'+'<img src="' + basePath + 'resource/images/icon/download.png" ></a>';
			}
			return '<a href="' + basePath + 'common/downloadbyId.action?id='+val.split(";")[1]+'" style="text-decoration:none""><span style="color:green;padding-left:2px;">' + val.split(";")[0] + '</span>'+'<img src="' + basePath + 'resource/images/icon/download.png" ></a>';
		}else return val;
	},
	
	/**	  
	 * BOM模具加工方式明细行   程序编号附件上传下载
	 * */
	detailAttachCustom:function(val, meta, record, x, y, store, view){	
		if(record&&record.data["1progAttach"]!=null&&record.data["1progAttach"]!=""){		
			var attach=record.data["1progAttach"];
			var grid = view.ownerCt,column = grid.columns[y],field = column.dataIndex;
				if(record.data[field] != attach){
				record.set(field,attach);
			}
			return '<a href="' + basePath + 'common/downloadbyId.action?id='+attach.split(";")[1]+'"><img src="' + basePath + 'resource/images/icon/download.png" ></a>'+'<img src="'+basePath+'resource/images/renderer/attach.png">'+'<span style="color:green;padding-left:2px;">' + attach.split(";")[0] + '</span>';
		}else if(record&&val!=null&&val!=""){
			return '<a href="' + basePath + 'common/downloadbyId.action?id='+val.split(";")[1]+'"><img src="' + basePath + 'resource/images/icon/download.png" ></a>'+'<img src="'+basePath+'resource/images/renderer/attach.png">'+'<span style="color:green;padding-left:2px;">' + val.split(";")[0] + '</span>';
		}else return val;
	},
	
	/**	  
	 * BOM模具加工方式明细行   图纸编号附件上传下载
	 * */
	detailAttachPicture:function(val, meta, record, x, y, store, view){	
		if(record&&record.data["1pictAttach"]!=null&&record.data["1pictAttach"]!=""){		
			var attach=record.data["1pictAttach"];
			var grid = view.ownerCt,column = grid.columns[y],field = column.dataIndex;
				if(record.data[field] != attach){
				record.set(field,attach);
			}
			return '<a href="' + basePath + 'common/downloadbyId.action?id='+attach.split(";")[1]+'"><img src="' + basePath + 'resource/images/icon/download.png" ></a>'+'<img src="'+basePath+'resource/images/renderer/attach.png">'+'<span style="color:green;padding-left:2px;">' + attach.split(";")[0] + '</span>';
		}else if(record&&val!=null&&val!=""){
			return '<a href="' + basePath + 'common/downloadbyId.action?id='+val.split(";")[1]+'"><img src="' + basePath + 'resource/images/icon/download.png" ></a>'+'<img src="'+basePath+'resource/images/renderer/attach.png">'+'<span style="color:green;padding-left:2px;">' + val.split(";")[0] + '</span>';
		}else return val;
	},
	
	/**	  
	 * 明细行报表上传下载
	 * */
	detailAttach1:function(val, meta, record, x, y, store, view){	
		var file_path = record.data["file_path"];
		if(record&&record.data["1detailAttach"]){		
			var attach=record.data["1detailAttach"];			
			var grid = view.ownerCt,column = grid.columns[y],field = column.dataIndex;
				if(record.data[field] != attach){
				record.set(field,attach);
			}
			return '<img src="'+basePath+'resource/images/renderer/attach.png">'+'<span style="color:green;padding-left:2px;">' + attach.split(";")[0] + '</span>'+'<a href="' + basePath + 'common/download.action?path='+ file_path +'"><img src="' + basePath + 'resource/images/icon/download.png" ></a>';
		}else if(record &&en_admin=="pdf"){
			if(val!=null&&val!="" ){
		       return '<img src="'+basePath+'resource/images/renderer/attach.png">'+'<span style="color:green;padding-left:2px;">' + val.split(";")[0] + '</span>'+'<a href="' + basePath + 'common/download.action?path='+ file_path +'"><img src="' + basePath + 'resource/images/icon/download.png" ></a>';
			}else if(!record.dirty && record.data["file_name"] !="" && record.data["file_name"]!=null){			  
			  return '<img src="'+basePath+'resource/images/renderer/attach.png">'+'<span style="color:green;padding-left:2px;">' + record.data["file_name"]+'.rpt'+ '</span>'+'<a href="' + basePath + 'common/download.action?path='+ file_path +'"><img src="' + basePath + 'resource/images/icon/download.png" ></a>';
			}else if(record.data["id"] !="" && record.data["id"]!=null&&en_admin=="pdf"){//解决更新条件等字段时附件字段为空无法更新
				var arr=file_path.split('/');var length=arr.length;
				 record.set('attach',arr[length-1]+";0");
				 return '<img src="'+basePath+'resource/images/renderer/attach.png">'+'<span style="color:green;padding-left:2px;">' + arr[length-1]+ '</span>'+'<a href="' + basePath + 'common/download.action?path='+ file_path +'"><img src="' + basePath + 'resource/images/icon/download.png" ></a>';
			}
		}else return val;
	},
	/**
	 *kpi考核结果 查看分数来源
	 * */
	score_from:function(val,meta,record){
		if(record.data.ktd_kiid!=0){
			var ktd_kiid=record.data.ktd_kiid;
			return "<input type='button' value='查看分数来源' name='"+ktd_kiid+"' style='color:gray;font-size:13px;cursor:pointer;height:25px;' onClick='scoreFrom(this.name)' />";
		}
		return '';
	},
	/**
	 * 培训资源链接
	 */
	resource_link:function(val){
		return '<a target="_blank" href="' + val +'">'+val+'</a>';	
	},
	/**
	 * 培训计划—考试-特殊颜色
	 */
	 trainingPlan_exam:function(val,meta,record){
	 	if(record&&record.data['ti_exam']==true){
	 		meta.tdCls = "x-grid-cell-renderer-cl";
	 	}
	 	return val;
	 },
	 /**
	  * 明细行texttrigger
	  */
	 texttrigger:function(val, meta, record, x, y, store, view){		
	 	var grid = view.ownerCt,column = grid.columns[y];
	 	meta.style="padding-right:0px!important";
	 	if(val){
	 		var id = grid.id;
	 		return  '<span style="display:inline-block;padding-left:2px;width:80%; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;">'+val+'</span>'+
	 				'<span><img src="'+basePath+'resource/images/renderer/texttrigger.png" style="display: inline; float: right;" ' +
	 						'onClick="Ext.getCmp(\''+id+'\').showTrigger(' +  '\''+escape(val)+'\'' +')"></span>';
	 	}
	 	return '';
	 },
	  /**
	  * 明细行texttrigger支持换行
	  */
	 texttrigger2:function(val, meta, record, x, y, store, view){		
	 	var grid = view.ownerCt,column = grid.columns[y];
	 	meta.style="padding-right:0px!important";
	 	if(val){
	 		if(!window.showTrigger) {
		 		window.showTrigger = function showTrigger(gridId,val,name,x,y){//明细行文本框
					val = unescape(val);
					var store = Ext.getCmp(gridId).store;
					var record = store.getAt(x);
					Ext.MessageBox.minPromptWidth = 600;
				    Ext.MessageBox.defaultTextHeight = 200;
				    Ext.MessageBox.style= 'background:#e0e0e0;';
				    Ext.MessageBox.prompt("详细内容", '',
				    function(btn, text) { 
				        if (btn == 'ok') {
				            if (name&&record) {
				                record.set(name, text);
				            }
				        }
				    },
				    this, true, //表示文本框为多行文本框    
				    val);
				};
	 		}
	 		return  '<span style="display:inline-block;padding-left:2px; width:80%;text-overflow: ellipsis; white-space:nowrap; overflow:hidden;">'+val+'</span>'+
	 				'<span><img src="'+basePath+'resource/images/renderer/texttrigger.png" style="display: inline; float: right;" ' +
	 				'onClick="window.showTrigger(\'' +grid.id+ '\',\''+escape(val)+'\',\''+column.dataIndex+'\','+x+','+y+');"></span>';
	 	}
	 	return '';
	 },
	 /**
	  * CommonUse 列表链接
	  */
	 openCommonUse:function(val, m, record, x, y, store, view) {
		var grid = view.ownerCt, me = grid.RenderUtil,column = grid.columns[y], url = record.get('cu_url');
		if(url) {
			var val = '<a class="x-btn-link" onclick="openTable1(' 
							+ record.get['cu_id'] + ",null,\'" + val + "\',\'" + url.replace(/\'/g, '\\\'') + "\',null,null,null,null,true)\">" + val + "</a>";
			return val;
		}
		return val;
	 }, 
	 /**
	 * @PM.MRP
	 * MRP 需求中的订单号根据不同来源[销售订单，销售预测]打开链接
	 */
	openorder: function(val, meta, record) {
		if(!Ext.isEmpty(val)) {
			if(!window.__fn) {
				var fn = function(code) {
					Ext.Ajax.request({
						url: basePath + 'common/getFieldData.action',
						params: {
							caller: 'SaleForecast',
							field: 'sf_id',
							condition: 'sf_code=\'' + code + '\''
						},
						callback: function(opt, s, r) {
							if(s) {
								var rs = Ext.decode(r.responseText);
								if(rs.data != null && rs.data > 0) {
									openUrl('jsps/scm/sale/saleForecast.jsp?whoami=SaleForecast&formCondition=sf_idIS' + rs.data + 
											'&gridCondition=sd_sfidIS' + rs.data);
								}else{
									openUrl('jsps/scm/sale/sale.jsp?whoami=Sale&formCondition=sa_codeIS' + code + 
											'&gridCondition=sd_codeIS'+code);
								}
							}
						}
					});
				};
				window.__fn = fn; 
			}
			val = '<a href="javascript:window.__fn(\'' + val + '\');">' + val + '</a>';
		}
		return val;
	},
	/**
	 * @PM.GoodsUpDeal  批量上架
	 * 本次上架数量不允许超过建议上架数量，不允许小于0
	 */
	pm_goodsup_thisqty: function(val, meta, record, x, y){
		meta.tdCls = "x-grid-cell-renderer-bl";
		var r=0;
		//r为该序号总剩余未发料数
		r = record.data['mdd_qty'];	
		if(record.dirty){//未被修改过，并且mdd_qty与计算值不等
		     if(val > r) {
				   showError("本次上架数不得超过" + r);
				   val = r;
				   record.set('mdd_upqty', val);
			 } else if(val < 0 || val == 0){
				   showError("本次上架数必须大于0");
				   if(val!=r){
					   val = r;
					   record.set('mdd_upqty', val);
				   }
				  
			 }
		     
		}else if(val == 0 ){
			 if(val!=r){
				   val = r;
				   record.set('mdd_upqty', val);
			   }
		}
		return val;
	},
	/**
	 * @pm 工艺路线工序
	 * @expression price_formula:pricePerTime*cd_standworkhour
	 * 
	 */
	price_formula: function(val, meta, record, x, y, store, view){
		var grid = view.ownerCt,me = grid.RenderUtil,
			column = grid.columns[y],field = column.dataIndex;
		var hour = record.data['cd_standworkhour'];
		var man = record.data['cd_standmancount'];
		var _val = pricePerTime*hour;
		//用原值赋值
		if(_val != val) {
			val = _val;
			record.set(field, _val);
			val = _val;
		}				
		return val.toFixed(6);
	},
	/**
	 * @plm 测试单生成BUG单
	 */
	checklist_toBug:function(val,meta,record){
		if(record.data.ch_result=='NG'&&record.data.ch_cbdcode==''){
			
			return "<input type='button' value='NG状态生成BUG' name='BUGbutton' style='color:gray;font-size:13px;cursor:pointer;height:25px;' onClick='updateCheckBase()'>";
		}
		return '';
	},
	/**
	 *  maz  批量询价单  物料详情
	 */
	product_detail:function(val,meta,record){
		var pr_code = record.data.bip_prodcode;
		var c = record.data.c;
		if(pr_code!='' && c>0){
			return "<input type='button' value='查看具体报价' name='"+pr_code+"' style='padding: 0 5px 0 5px;color:#4B4B4B;font-size:13px;cursor:pointer;height:25px;weigh:35px;background-image: linear-gradient(0deg,#d4d4d4,#ffffff 70%);display: inline-block;cursor: pointer;text-align: center;border-radius: 3px;' onClick='productDetail(this.name)'>";
		}
		return '';
	},
	/**
	 * 批量结案界面制造单号render
	 */
	linkProdInOut: function(val, m, record, x, y, store, view) {
		var grid = view.ownerCt;
		var me = grid.RenderUtil;
		var column = grid.columns[y];
		if(val != null){
			if(!window.__fn){
				var fn = function(macode){
                       var win = Ext.getCmp('history-win');
                       if(win == null){
                            win = Ext.create('Ext.window.Window', {
                                id: 'history-win',
                                width: '80%',
                                height: '90%',
                                maximizable : true,
                                layout: 'anchor',
                                closeAction: 'hide',
                                setMyTitle: function(macode){//@param macode 工单号
                                    this.setTitle('工单号:<font color=blue>' + macode + '</font>&nbsp;的出入库明细&nbsp;&nbsp;');
                                },
                                reload: function(macode){//@param macode 工单号
                                    var g = this.down('grid[id=history]');
                                    g.GridUtil.loadNewStore(g, {
                                        caller: g.caller,
                                        condition: "pd_ordercode ='" + macode + "' order by pi_status desc"
                                    });
                                    this.setMyTitle(macode);
                                }
                            });
                            win.setMyTitle(macode);
                            win.show();
                            win.add(Ext.create('erp.view.core.grid.Panel2', {
                                id: 'history',
                                anchor: '100% 100%',
                                caller: 'ProdInOut!Make!AllHistory',
                                condition: "pd_ordercode ='" + macode + "' order by pi_status desc",
                                bbar: null
        
                            }));
                        } else {
                            win.reload(macode);
                            win.show();
                        }
                    }
				window.__fn=fn;
			}
			val = '<a href="javascript:window.__fn(\'' + val + '\');">' + val + '</a>';
		}
		return val;
	},
	/**
	 * @SCM.PURC.Purchase
	 * 采购单明细行报关价根据物料品牌自动计算(富为)
	 */
	scm_purc_bgprice: function(val, meta, record){
		meta.style = "background:#C6E2FF;";
		if(!Ext.isNumber(val)) {
			record.set('pd_bgprice', 0);
			return 0;
		} else {
			var bg = record.data['pd_bgprice'], rate = record.data['pb_rate'], price = record.data['pd_price'];
			if(bg == 0 && rate && price) {
				val = Ext.Number.toFixed(price/rate, 4);
				record.set('pd_bgprice', val); 
			}
			return val;
		}
	},
	/**
	 * Checklist单关联BUG
	 */
	checklist_to_bug: function(val,meta,record){
		var url='jsps/plm/test/check.jsp?';
		var keyValue=record.data.cbd_cldid==null? val : record.data.cbd_cldid;
		var code = record.data.cbd_name==null?record.data.cld_name : record.data.cbd_name;
		url+='&formCondition=cld_idIS'+keyValue+'&gridCondition=ch_cldidIS'+keyValue;
		if(code!=null&&code!=""&&keyValue!=""){
			return '<a href="javascript:openUrl(\''+url+'\');">'+'查看BUG单'+'</a>';
		}else{
			return ' ';
		}
	},
	/**
	 * EDI从表默认币别USD
	 */
	default_EDI_currency: function(val,meta,record){
		if(record!=null&&record!=""){
			return 'USD';
		}else{
			return ' ';
		}
	},
	/**
	 * 销售订单转出货单(信扬)
	 * 计算用款天数
	 */
	scm_sale_usedays: function(val, m, record, x, y, store, view){
		m.style = "background:#C6E2FF;";
		var form = view.ownerCt.ownerCt.down('form'), flowdate = form.down('field[name=sa_flowdate]'), 
			apdate = record.data['sa_apdate_user'];
		val = 0;
		if(flowdate && !Ext.isEmpty(flowdate.value)){
			flowdate = new Date(Ext.Date.format(flowdate.value,'Y-m-d'));
			if(apdate && !Ext.isEmpty(apdate)){
				apdate = new Date(Ext.Date.format(apdate,'Y-m-d'));
				val = Math.ceil((flowdate.getTime() - apdate.getTime())/(86400000)); // 计算间隔天数    	
			}
		}
		return val;
	},
	/**
	 * 订阅项批量检测
	 * 未通过检测项目标红
	 */
	subsbatchtest_checked: function(val,meta,record){
		if(val == 0){
			return '<font style="color:red;">否</font>';
		}else{
			return '是';
		}
	},
	/**
	 * 注重
	 *
	 */
	needRemind: function(val,meta,record){
		if(val){
			return '<font style="color:red;">'+val+'</font>';
		}else{
			return val;
		}
	},
	adthisqty : function(val, meta, record, x, y, store, view){
		meta.tdCls = "x-grid-cell-renderer-bl";	
		var grid = view.ownerCt, me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex,
		format = (column.format || '0,000.00'), perc = format.substring(format.indexOf('.') + 1).length;
		var field = this.columns[y].dataIndex;

		var repkind=record.data['repkind'];//主替代料
		var adqty=record.data['ad_qty'];//数量
		var adyqty=record.data['ad_yqty'];//已转数量
		var needqty=record.data['needqty'];//需求总数
		var  v=Number(adqty)-Number(adyqty);
		var maxValue=0;
		if(repkind=='替代料'){//repkind=替代料 最大数量不超过needqty-ad_yqty
			maxValue=Number(needqty)-Number(adyqty);
		}else{
			maxValue=Number(adqty)-Number(adyqty);
		}
		val = (val == null || val == 0) ? v : val;
		if(record.data[field] != val){
			record.set(field, val);
		}
		//鉴于小数问题 保留2位小数
		if(column.editor || (column.getEditor && column.getEditor())) {//在允许编辑的情况下，修改值不能大于maxValue
			val = (!Ext.isNumber(val) || val == 0) ? v : val;	
			if(Number(val) > Ext.Number.toFixed(maxValue, perc) ){
				val = v;
				showError('请不要输入超过最大数量' + maxValue + "的值!");
			}
		} else {
			val = v;
		}
		var f = Ext.Number.toFixed(val, perc), v = record.get(field), _v = val;
		if(column.xtype == 'numbercolumn'){
			_v = Ext.util.Format.number(val, column.format);
		}
		if( f != v) {
			record.set(field, f);
		}
		return _v; 
	},
	
	/**
	 * 工作日报列表Html标签"<"、">"转义
	 * 反馈编号：2017070185 
	 * @author lidy
	 * @since 2017-9-21
	 */
	htmlScriptTagEscape : function(val, meta, record){
		val = val.replace(/</g,'&lt;');
		val = val.replace(/>/g,'&gt;');
		return val;
	},
	/**
	 * 包装单件数为0，算合计数时默认为1
	 * 
	 */
	cbm:function(val,meta,record){
		var pd_cartons=record.data['pd_cartons'];//件数
		var pd_qty=record.data['pd_qty'];//数量
		if(pd_cartons==0){
			pd_cartons=1;
		}
		return pd_cartons*pd_qty;
	},
	/**
	 * 查看物料资料附件
	 * */
	getProductFileDetails:function(val,meta,record){
		
		if(record.data['pr_attach']!=null && record.data['pr_attach'] !=""){
			return "<input type='button' value='查看附件' name='getProductFileDetails' style='color:gray;font-size:13px;cursor:pointer;height:25px;align:center;' onClick='getProductFileWindow(\""+record.data['pr_attach']+ "\" )') />";
		}else{
			return null;//UI设计需求隐藏无附件 "<input type='button' value='无附件' name='getProductFileDetails' style='color:gray;font-size:13px;cursor:pointer;height:25px;align:center;' onClick='') />";
		}
		
	},
	
	/**
	 * 新成套发料界面render
	 */
	pm_make_thisqty_new : function(val, meta, record, x, y){
		meta.tdCls = "x-grid-cell-renderer-bl";
		var t = 0,r=0,busy = Ext.getCmp('editorColumnGridPanel').busy;
		var ta = record.data['mm_oneuseqty'].toString().split(".");
    	var length = ta[1]?ta[1].length:0;
		//r为该序号总剩余未发料数
		r = record.data['mm_qty'] - (record.data['mm_havegetqty'] - record.data['mm_addqty'] + record.data['mm_returnmqty'] )  - record.data['mm_totaluseqty'] -  record.data['mm_stepinqty'] ; 
		if(record.data.isrep) {
			t = record.data['mm_qty'] - (record.data['mm_havegetqty'] - record.data['mm_addqty'] + record.data['mm_returnmqty'] )  - record.data['mm_totaluseqty'] -  record.data['mm_stepinqty']; 
		} else {
			t = record.data['mm_qty'] - record.data['mm_canuserepqty'] - (record.data['mm_havegetqty'] - record.data['mm_haverepqty'] + 
					record.data['mm_returnmqty'] - record.data['mm_repreturnmqty'] - record.data['mm_addqty'] + record.data['mm_repaddqty']) - 
					record.data['mm_totaluseqty']+record.data['mm_repqty']-  record.data['mm_stepinqty']; 
		}		
		//判断单位用量的小数位数,本次退料的小数位数与单位用量的相同
      	t = t.toFixed(length)-0;
      	r = r.toFixed(length)-0;
        //大于总剩余未发料数则默认等于剩余未发料数
        t = t > r ? r : t;
		t = t < 0 ? 0 : t;
		var max = t;
		max = max < 0 ? 0 : max;
		//ma_thisqty*mm_oneuseqty
		var tqty = max;
		if(Ext.getCmp('set') //form+detail 发料界面 没有这个字段
				&& Ext.getCmp('set').value) {
			var items = Ext.getCmp('grid').store.data.items,mItem = null;
			if(ifIncludingLoss){//套料发料包含损耗
				Ext.each(items, function(item){
					if(item.data['ma_code'] != null && item.data['ma_code'].toString().trim() != '' 
						&& item.data['ma_code'] == record.data['mm_code']){
						mItem = item;
						if(item.data['ma_thisqty'] > 0){
							max = item.data['ma_thisqty'] * record.data['mm_qty']/item.data['ma_qty'];
							if(parseInt(record.data['mm_qty'])== record.data['mm_qty']){//需求数为整数，取整
								max = Math.ceil(max);
							}
							max = max.toFixed(length);
							tqty = Math.min(max, t);
							tqty = Number(tqty);
						}
					}
			    });
			}else{
				Ext.each(items, function(item){
					if(item.data['ma_code'] != null && item.data['ma_code'].toString().trim() != '' 
						&& item.data['ma_code'] == record.data['mm_code']){
						mItem = item;
						if(item.data['ma_thisqty'] > 0 && record.data['mm_oneuseqty'] > 0){
							max = item.data['ma_thisqty'] * record.data['mm_oneuseqty'];
							max = max.toFixed(length);
							tqty = Math.min(max, t);
							tqty = Number(tqty);
						}
					}
			    });
			}						
		}
		val = Number(val);
		if(!record.dirty){//未被修改过，并且mm_thisqty与计算值不等
		  if(val != tqty){
			val = tqty;
			Ext.defer(function(){
				record.set('mm_thisqty', val);
			},10);
		  }
		}else{
		     if(val > tqty) {
				//如果参数设置为不考虑可替代数
				if (ifCanrepqty != true){
					if(!busy)
						showError("本次领料数不得超过" + tqty);
				}else{
					if(val > r){			
						showError("本次领料数不得超过" + r);
					}
				}
				val = tqty;
				Ext.defer(function(){
					record.set('mm_thisqty', val);
				},10);
			} else if(val < 0){
				if(!busy)
					showError("本次领料数不能是负数");
				val = tqty;
				Ext.defer(function(){
					record.set('mm_thisqty', val);
				},10);
			} else if(val == 0 && tqty != 0){
				val = tqty;
				Ext.defer(function(){
					record.set('mm_thisqty', val);
				},10);
			}
		}
		return val;
	},
	
	/**
	 * 新成套补料界面 render
	 *
	 */
	pm_make_thisadd_new : function(val, meta, record, x, y) {
		var form = Ext.getCmp("dealform");
		var addNotBalance = form.items.get('addNotBalance').value;
		meta.tdCls = "x-grid-cell-renderer-bl";
		var t = 0,busy = Ext.getCmp('editorColumnGridPanel').busy;
		var ta = record.data['mm_oneuseqty'].toString().split(".");
		var length = ta[1]?ta[1].length:0;
		if(record.data.isrep) {
			return val;
		} else {
			if(record.data['mm_qty'] == Math.floor(record.data['mm_qty'])){
				t = record.data['mm_scrapqty'] + record.data['mm_returnmqty'] 
			           - record.data['mm_addqty']-record.data['MM_TURNADDQTY']-  record.data['mm_stepinqty']+ record.data['mm_clashqty'];// - record.data['mm_totaluseqty']
			}else{
				t = record.data['mm_scrapqty'] + record.data['mm_returnmqty']
			           - record.data['mm_addqty'] - record.data['MM_TURNADDQTY']-  record.data['mm_stepinqty']+ record.data['mm_clashqty'];
			}	
			//是否包含备损数
			if(!addNotBalance){
				t = t - record.data['mm_balance'];
			}
			//判断单位用量的小数位数,本次退料的小数位数与单位用量的相同
			t = t.toFixed(length);
		}
		var max = t;
		max = max < 0 ? 0 : max;
		var tqty = max;
		if(Ext.getCmp('editorColumnGridPanel')){
			var items = Ext.getCmp('editorColumnGridPanel').store.data.items;
			Ext.each(items, function(item){
				if(item.data['ma_code'] != null && item.data['ma_code'].toString().trim() != '' 
					&& item.data['ma_code'] == record.data['mm_code']){
					mItem = item;
					if(item.data['ma_thisqty'] > 0 && record.data['mm_oneuseqty'] > 0){
						max = item.data['ma_thisqty'] * record.data['mm_oneuseqty'];
						max = max.toFixed(length);
						tqty = Math.min(max, t);
					}
				}
			});
		}
		if(val > tqty) {
			if(!busy)
				showError("本次补料数不得超过" + tqty);
			val = tqty;
			record.set('mm_thisqty', val);
		} else if(val < 0){
			if(!busy)
				showError("本次补料数不能是负数");
			val = tqty;
			record.set('mm_thisqty', val);
		} else if(val == 0 && tqty != 0){
			val = tqty;
			record.set('mm_thisqty', val);
		}
		return val;
	},
	/**
	 * 新界面成套退料 render
	 */
	pm_make_rqtyf_new: function(val, meta, record, x, y, store){
		meta.style = "background:#C6E2FF;";
		if(record.data['mm_onlineqty'] < 0) {// 返修工单退料
			return val;
		}
		var mrec = record;
		if(record.data.isrep) {
			//替代料 本次数量 按主料本次数量计算
			store.each(function(d){
				if(d.data.mm_id == record.data.mm_id && !d.data.isrep) {
					mrec = d;return;
				}
			});
		}
		var t = Math.floor(((mrec.data['mm_onlineqty'] || val) - (mrec.data['mm_backqty'] || 0) - (mrec.data['mm_stepinqty'] || 0) )),
		busy = Ext.getCmp('editorColumnGridPanel').busy;
		var max = t;
		max = max < 0 ? 0 : max;
		var tqty = max;
		var items = Ext.getCmp('editorColumnGridPanel').store.data.items;
		Ext.each(items, function(item){
			  if(item.data['ma_code'] != null && item.data['ma_code'].toString().trim() != '' 
				  && item.data['ma_code'] == mrec.data['mm_code']){
				  mItem = item;
				  if(item.data['ma_thisqty'] > 0 && mrec.data['mm_oneuseqty'] > 0){
					  max = item.data['ma_thisqty'] * mrec.data['mm_oneuseqty'];
					  tqty = Math.min(max, t);
				  }
			  }
		});		
		if(val > tqty) {
			if(!busy)
				showError("本次退料的数量不得超过" + tqty);
			val = tqty;
		} else if(val < 0){
			if(!busy)
				showError("本次退料的数量不能是负数");
			val = tqty;
		} else if(val == 0 && tqty != 0){
			val = tqty;
		}
		if(record.get('mm_thisqty') != val)
			record.set('mm_thisqty', val);
		return val;
	},
	/**
	 * 新成套报废界面 render
	 */
	pm_make_rqty_new: function(val, meta, record, x, y, store){
		meta.style = "background:#C6E2FF;";
		if(record.data['mm_onlineqty'] < 0) {// 返修工单退料
			return val;
		}
		var mrec = record;
		if(record.data.isrep) {
			//替代料 本次数量 按主料本次数量计算
			store.each(function(d){
				if(d.data.mm_id == record.data.mm_id && !d.data.isrep) {
					mrec = d;return;
				}
			});
		}
		  //判断单位用量的小数位数,本次退料的小数位数与单位用量的相同
		var length = mrec.data['mm_oneuseqty'].toString().split(".")[1]?mrec.data['mm_oneuseqty'].toString().split(".")[1].length:0;
		var t = ((mrec.data['mm_onlineqty'] || val) - (mrec.data['mm_backqty'] || 0)),
		busy = Ext.getCmp('editorColumnGridPanel').busy;
		t = t.toFixed(length);
		var max = t;
		max = max < 0 ? 0 : max;
		var tqty = max;
		var items = Ext.getCmp('editorColumnGridPanel').store.data.items;
		Ext.each(items, function(item){
			  if(item.data['ma_code'] != null && item.data['ma_code'].toString().trim() != '' 
				  && item.data['ma_code'] == mrec.data['mm_code']){
				  mItem = item;
				  if(item.data['ma_thisqty'] > 0 && mrec.data['mm_oneuseqty'] > 0){
					  max = item.data['ma_thisqty'] * mrec.data['mm_oneuseqty'];
					  max = max.toFixed(length);
					  tqty = Math.min(max, t);
				  }else if(item.data['ma_remainqty'] > 0 && mrec.data['mm_oneuseqty'] > 0){
					  var nQty = item.data['ma_remainqty'];
					  var pr_lossrate;
					  if(mrec.data['pr_lossrate']){
						  pr_lossrate = mrec.data['pr_lossrate'];
					  }else{
						  pr_lossrate = 0;
					  }
					  if(mrec.data['mm_qty']==Math.floor(mrec.data['mm_qty'])){
						  max = Math.floor(mrec.data['mm_havegetqty']+mrec.data['mm_totaluseqty']-mrec.data['mm_scrapqty']-mrec.data['mm_oneuseqty']*nQty*(1+pr_lossrate/100));
					  }else{
						  max = mrec.data['mm_havegetqty']+mrec.data['mm_totaluseqty']-mrec.data['mm_scrapqty']-mrec.data['mm_oneuseqty']*nQty*(1+pr_lossrate/100);
						  max = max.toFixed(length);						 
					  }
					  max = max<0?0:max;
					  tqty = Math.min(max, t);
				  }
			  }
		});	
		if(val > tqty) {
			if(!busy)
				showError("本次填写的数量不得超过" + tqty);
			val = tqty;
		} else if(val < 0){
			if(!busy)
				showError("本次填写的数量不能是负数");
			val = tqty;
		} else if(val == 0 && tqty != 0){
			val = tqty;
		}
		if(val && record.get('mm_thisqty') != val){
			
			record.set('mm_thisqty', val);
		}
		return val;
	},
	/**
	 * 通用方法 同A floating:B
	 * 颜色相反,绿色向下,红色向上的箭头
	 * 比较A相对于B的浮动变化
	 * @expression floating:B
	 */
	crossfloating: function(val, meta, record, x, y, store, view) {
		var grid = view.ownerCt,me = grid.RenderUtil,column = grid.columns[y],field = column.dataIndex;
		if(!me || !me.args){
			return val;
		}
		var arg = me.args.floating[field], a = null, b = null, f = null, n = null, istxt = true;
		if(column.xtype == 'datecolumn') {
			if(!val) return null;
			a = record.get(arg);
			b = val; 
			f = (column.format || 'Y-m-d');
			n = Ext.Date.format(val, f);
			istxt = false;
		} else if(column.xtype == 'numbercolumn') {
			a = Number(record.get(arg));
			b = Number(val); 
			f = (column.format || '0,000');
			n = Ext.util.Format.number(val, f);
			istxt = false;
		} else {
			a = record.get(arg);
			b = val; 
		}
		if(istxt) {
			if(a != b) {
				return '<span style="color:red;padding-left:2px">' + val + '</span>';
			} else {
				return val;
			}
		} else {
			if(a < b) {
				return '<img src="' + basePath + 'resource/images/16/redup.png">' + 
				'<span style="color:red;padding-left:2px">' + n + '</span>';
			} else if(a > b) {
				return '<img src="' + basePath + 'resource/images/16/greendown.png">' + 
				'<span style="color:red;padding-left:2px">' + n + '</span>';
			} else if(a == 0 && b == 0) {
				return '';
			}
		}
		return n;
	},
	/**
	 * 预警项目参数选择配置
	 */
	sys_alert_item_param_dbfind: function(val,meta,record) {	
		if(!this.addedListeners){
			var idx1,idx2,idx3;
			for(var i=0;i<this.columns.length;i++) {
				if(this.columns[i].dataIndex=='aa_type') {
					idx1=i;
				}else if(this.columns[i].dataIndex=='aa_dbfind'){
					idx2=i;
				}else if(this.columns[i].dataIndex=='aa_width') {
					idx3=i;
				}
			}
			this.columns[idx1].editor?this.columns[idx1].editor.listeners={
				change:function(combo,record,e){
					var grid = Ext.getCmp('grid'),
		                sel = grid.getSelectionModel().getSelection()[0];
		            sel.data['aa_dbfind'] = '';
				}
			}:'';
			this.columns[idx2].editor?this.columns[idx2].editor.listeners={
				expand:function(combo,e){
					var data=[], grid = Ext.getCmp('grid'), 
						sel = grid.getSelectionModel().getSelection()[0],
		                type = sel.data['aa_type'], arr = [];
		                
		            if(type=='S') {
		            	arr = [{display:'包含',value:'vague'},{display:'不包含',value:'novague'},{display:'开头是',value:'head'},{display:'结尾是',value:'end'},{display:'等于',value:'direct'},{display:'不等于',value:'nodirect'}];
		            }else if(type=='N') {
		            	arr = [{display:'等于',value:'='},{display:'大于',value:'>'},{display:'大于等于',value:'>='},{display:'小于',value:'<'},{display:'小于等于',value:'<='},{display:'不等于',value:'<>'}];
		            }else if(type=='D') {
		            	arr = [{display:'等于',value:'='},{display:'开始于',value:'>='},{display:'结束于',value:'<='}];
		            }else if(type=='YN' || type=='C' || type=='R') {
		            	arr = [{display:'等于',value:'='},{display:'不等于',value:'<>'}];
		            }else if(type=='CBG') {
		            	arr = [{display:'属于',value:'in'},{display:'不属于',value:'not in'}]
		            }
		           	for(var i=0;i<arr.length;i++){
		                data.push([arr[i].display,arr[i].value]);
		            }
	                combo.clearValue();
		            combo.store.loadData(data);
				}
			}:'';
			this.columns[idx3].editor?(this.columns[idx3].editor.maxValue=4):'';
			this.columns[idx3].editor?(this.columns[idx3].editor.minValue=0):'';
			this.addedListeners = true;
		}
		// 正常的renderer逻辑
		var type = record.get('aa_type'),arr=[];
		if(type=='S') {
        	arr = [{display:'包含',value:'vague'},{display:'不包含',value:'novague'},{display:'开头是',value:'head'},{display:'结尾是',value:'end'},{display:'等于',value:'direct'},{display:'不等于',value:'nodirect'}];
        }else if(type=='N') {
        	arr = [{display:'等于',value:'='},{display:'大于',value:'>'},{display:'大于等于',value:'>='},{display:'小于',value:'<'},{display:'小于等于',value:'<='},{display:'不等于',value:'<>'}];
        }else if(type=='D') {
        	arr = [{display:'等于',value:'='},{display:'开始于',value:'>='},{display:'结束于',value:'<='}];
        }else if(type=='YN' || type=='C' || type=='R') {
        	arr = [{display:'等于',value:'='},{display:'不等于',value:'<>'}];
        }else if(type=='CBG') {
        	arr = [{display:'属于',value:'in'},{display:'不属于',value:'not in'}]
        }
        var rv = val;
        for(var i=0;i<arr.length;i++) {
        	if(arr[i].value==val) {
        		rv = arr[i].display;
        	}
        }
        return rv;
	},
	/**
	 * 预警项目打开下拉框配置
	 * */
	getComboSetWindow:function(val,meta,record){
		getComboSetWindow = function(index,values){
			var me = this;
			Ext.create('erp.view.core.window.AlertComboSet',{index:index,values:values});
		}
		return '<div unselectable="on" class="x-grid-cell-inner x-unselectable" style="padding-right:0px!important; text-align: left;"><span style="display:inline-block;padding-left:2px;width:80%; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;">'+val+'</span><span><img src="'+basePath+'resource/images/renderer/texttrigger.png" style="display: inline; float: right;cursor: pointer;" onClick="getComboSetWindow(\''+record.index+'\',\''+val+ '\' )"></span></div>';
	},
	/**
	 * 预警项次推送策略
	 */
	alertInsCondition: function(val,meta,record) {
		conditionSqlWindow = function(index){
			var itemId = Ext.getCmp('aii_itemid').value;
			if(!itemId) {
				showMessage('提示', '请先选择预警项目!', 1000);
				return;
			}
			var win = Ext.create('erp.view.sys.alert.ConditionSqlWindow',{
				storeIndex: index
			});
			win.show();
		}
		return '<span style="display:inline-block;padding-left:2px; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;width: 80%;">'+val+'</span><span><img src="'+basePath+'resource/images/renderer/texttrigger.png" style="display: inline; float: right;cursor: pointer;" onClick="conditionSqlWindow(\''+record.get('aia_detno')+'\')"></span>';
	},
	/**
	 * 预警项次指定推送人
	 */
	alertInsAssign: function(val,meta,record) {
		assignSelectWindow = function(index){
			var win = Ext.create('erp.view.sys.alert.AssignSelectWindow2',{
				storeIndex: index
			});
			win.show();
		}
		return '<span style="display:inline-block;padding-left:2px; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;width: 80%;">'+val+'</span><span><img src="'+basePath+'resource/images/renderer/texttrigger.png" style="display: inline; float: right;cursor: pointer;" onClick="assignSelectWindow(\''+record.get('aia_detno')+'\')"></span>';
	},
	/**
	 * 预警项次条件推送人
	 */
	alertInsAssignSql: function(val,meta,record) {
		assignSqlWindow = function(index){
			var itemId = Ext.getCmp('aii_itemid').value;
			if(!itemId) {
				showMessage('提示', '请先选择预警项目!', 1000);
				return;
			}
			var win = Ext.create('erp.view.sys.alert.AssignSqlWindow2',{
				storeIndex: index
			});
			win.show();
		}
		return '<span style="display:inline-block;padding-left:2px; text-overflow: ellipsis; white-space:nowrap; overflow:hidden;width: 80%;">'+val+'</span><span><img src="'+basePath+'resource/images/renderer/texttrigger.png" style="display: inline; float: right;cursor: pointer;" onClick="assignSqlWindow(\''+record.get('aia_detno')+'\')"></span>';
	},
	/**
	 * 询价单跳转
	 * 
	 */
	inquirylink: function(val, m, record, x, y, store, view) {
		var grid = view.ownerCt, me = grid.RenderUtil,column = grid.columns[y], logic = column.logic, url = '';
		if(typeof(me)=='undefined'){
			 me = grid.ownerCt.RenderUtil;
		}
		if(record.data.kind=='采购询价单'){
			url = 'jsps/scm/purchase/inquiry.jsp?formCondition=in_idIS{in_id}&gridCondition=id_inidIS{in_id}';
		}else if(record.data.kind=='公共询价单'){
			url = 'jsps/scm/purchase/inquiryAuto.jsp?formCondition=in_idIS{in_id}&gridCondition=id_inidIS{in_id}';
		}else if(record.data.kind=='未报价公共询价单'){
			url = 'jsps/scm/purchase/batchInquiry.jsp?formCondition=bi_idIS{in_id}&gridCondition=bip_biidIS{in_id}';
		}
		if(logic||url) {
			var res = '';
			if(logic == 'necessaryField' || logic == 'orNecessField') {
				if(!val)
					res = '<img src="' + basePath + 'resource/images/icon/need.png" title="必填字段">';
			}
			var index = 0, length = url.length, s, e;
			while(index < length) {
				if((s = url.indexOf('{', index)) != -1 && (e = url.indexOf('}', s + 1)) != -1) {
					url = url.substring(0, s) + record.get(url.substring(s+1, e)) + url.substring(e+1);
					index = e + 1;
				} else {
					break;
				}
			}
			return res + '<a href="javascript:openUrl(\'' + url + '\');">' + val + '</a>';
		}
		return val;
	},
	/**
	 *查看图片
	 *只需要filePath表的path字段值 
	 * */
	PictureButton:function(val, m, record, x, y, store, view) {
		if(val&&val!=''){
			showPhoto = function(val){
				var me = this, resizer = me.resizer,
				imageframe = document.getElementById('ext-image-frame');
				src = basePath + 'common/download.action?path=' + val.replace(/\+/g, '%2B');
				if (!imageframe) {
					var el = Ext.DomHelper.append(document.body, '<img id="ext-image-frame" src="' + src +
							'" width="500" height="400" style="position:absolute;left:0;top:0;"/>', true);
					imageframe = el.dom;
				} else {
					imageframe.src = src;
				}
				if (!resizer) {
					resizer = this.resizer = Ext.create('Ext.resizer.Resizer', {
						target: 'ext-image-frame',
						pinned: true,
						width: 510,
						height: 410,
						minWidth: 100,
						minHeight: 80,
						preserveRatio: true,
						handles: 'all',
						dynamic: true
					});
					var resizerEl = resizer.getEl();
					resizerEl.on('dblclick', function(){
						resizerEl.hide(true);
					});
				}
				resizer.getEl().center();
				resizer.getEl().show(true);
				Ext.DomHelper.applyStyles(imageframe, 'position:absolute;z-index:100;');
			}
			return "<input type='button' value='查看图片' name='detailbutton' style='padding:2px;color:gray;font-size:13px;cursor:pointer;height:25px;' onClick='showPhoto(\""+val+"\")'>";
		}
	},
	/**
	 *@PLM 
	 * */
	MyBUGStatus:function(val,meta,record){
		var statuscode=record.data.cld_statuscode, url='';
		if(statuscode=='PENDING'){
			url += '<img src="' + basePath + 'resource/images/renderer/key2.png" title="'+val+'">';
		}else if(statuscode=='TESTING'){
			url +=  '<img src="' + basePath + 'resource/images/renderer/test.png" title="'+val+'">';
		}else if(statuscode=='HANDED'){
			url +=  '<img src="' + basePath + 'resource/images/renderer/finishrecord.png" title="'+val+'">';
		}else if(statuscode=='FINISH'){
			url +=  '<img src="' + basePath + 'resource/images/renderer/finishrecord.png" title="'+val+'">';
		}else if(statuscode=='NOTDEAL'){
			url +=  '<img src="' + basePath + 'resource/images/renderer/key1.png" title="'+val+'">';
		}else if(statuscode=='HANDUP'){
			url +=  '<img src="' + basePath + 'resource/images/renderer/key2.png" title="'+val+'">';
		}
		
		url += Ext.String.format('<a class="x-btn-link" onclick="openTable({0},\'Check\',\'BUG单\',\'jsps/plm/test/check.jsp\',\'cld_id\',\'ch_cldid\',null,null);">{1}</a>',
		record.get('cld_id'),
		val
		);
		return '<span>' + url + '<br><font color="#777">项目名称:'+record.get('cl_prjplanname')+'</font>'+'</span>';
	},
	/**
	 * 已报价询价替代料tooltip  maz
	 */
	InquiryTool : function(val, meta, record,x,y,store, view){	   
		var field = this.columns[y].dataIndex;
        var me = this.RenderUtil;
        var grid = view.ownerCt;
        var rfield = field;
        var caller = '';
        var cond = '';
        var width = '';
        if(me != null && me.args.InquiryTool != null){
        	rfield = me.args.InquiryTool[field][0].split('|')[0]; 
        	caller = me.args.InquiryTool[field][0].split('|')[1].split('=')[0];
        	cond = me.args.InquiryTool[field][0].split('|')[1].split('=')[1].split(',')[0];
        	width = me.args.InquiryTool[field][0].split('|')[1].split('=')[1].split(',')[1];
        }
        var code = record.data[rfield];
        var condition = cond+'='+ '\'' + code + '\'';
        if(code) { 
			if(!view.tip) {				
				view.tip = Ext.create('Ext.tip.ToolTip', {
					target: view.el,
					delegate: view.itemSelector,
					trackMouse: true,
					renderTo: Ext.getBody(),	
					maxWidth :830,
					listeners: {								          
						beforeshow: function updateTipBody(tip) {
							var rowindex = tip.triggerElement.rowIndex;
							code = Ext.getCmp(grid.id).getStore().getAt(rowindex-1).get(rfield);
					        condition = cond+'='+ '\'' + code + '\'';
					        var grid5 = Ext.getCmp(me.id+'_grid');
					        grid5.setLoading(true);//loading...
					        var param = {caller:caller,condition:condition};
					        if(!param._config) param._config=getUrlParam('_config');
					        Ext.Ajax.request({//拿到grid的columns
					        	url : basePath + "common/loadNewGridStore.action",
					        	params: param,
					        	method : 'post',
					        	callback : function(options,success,response){
					        		grid5.setLoading(false);
					        		var res = new Ext.decode(response.responseText);
					        		if(res.exceptionInfo){
					        			showError(res.exceptionInfo);return;
					        		}
					        		var data = res.data;
					        		code = data[0].id_prodcode;
					        		if(data[0].id_isreplace == 0){
					        			grid5.hide();											
										tip.update(code+'<br/> 非替代料报价');
					        		} else {
					        			tip.update('');
					        			grid5.setTitle(code);
				        				grid5.store.loadData(data);
				        				grid5.show();
					        		}
					        	}
					        });
						}
					},
					items : [Ext.create('erp.view.core.grid.Panel5', {
						id: me.id+'_grid',
						anchor: '100% 100%',
						caller: caller,
						condition: condition+' and id_isreplace=1',
						layout : 'fit',
						width:width,
						bbar: null
					})]
				});		
			}
        }		
		return record.data[field];
	},
	/**
	 * 模具资料查看附件
	 * */
	getModAlterFileDetails:function(val,meta,record,x,y,store, view){
		var form = Ext.getCmp('form');
		var grid = view.ownerCt;
		var keyField = form.keyField;
		var codeField = form.codeField;
		var detailMainKeyField = grid.keyField;
		var detailMainKeyValue;
        var keyValue = Ext.getCmp(keyField).value;
        var codeValue = Ext.getCmp(codeField).value;
		download=function(id){
			var me = this;
			var files = new Array();	
			if (!Ext.fly('ext-attach-download')) {  
				var frm = document.createElement('form');  
				frm.id = 'ext-attach-download';  
				frm.name = id;  
				frm.className = 'x-hidden';
				document.body.appendChild(frm);  
			}
			Ext.Ajax.request({
				url: basePath + 'plm/project/downloadbyId.action?id=' + id +'&folderId=0&_noc=1&canRead=1',
				method: 'post',
				form: Ext.fly('ext-attach-download'),
				isUpload: true,
				callback:function(options,success,resp){
					var begin = resp.responseText.indexOf('{"exceptionInfo":"');
					if(begin>-1){
						var end = resp.responseText.indexOf("\"}");
						var str = resp.responseText.substring(begin+'{"exceptionInfo":"'.length,end);
						showError(str);	
					}
				}
			});
		};
		updateLoadFileWindow=function(mainKeyValue){
			var win = new Ext.window.Window({
				id : 'win',
				height : 400,
				width : 902,
				maximizable : true,
				border:false,
				layout : 'anchor',
				title : '附件信息',
				bodyStyle : 'background:#F2F2F2;',
				items : [{
					id:'fileform',
		         	xtype:'form',
		        	layout:'column',
		        	width:'100%',
					height:'10%',	
		        	bodyStyle: 'background: transparent no-repeat 0 0;border: none;',
		        	items: [{
						xtype : 'filefield',
						name : 'file',
						buttonOnly : true,
						hideLabel : true,
						bodyStyle : 'margin-left:30',
						width : 60,
						id : 'file',
						buttonConfig : {
							iconCls : 'x-button-icon-pic',
							text : '上传',
							cls : 'x-btn-filefield'
						},
						listeners : {
							change : function(field) {
								var filename = '';
								if (contains(field.value, "\\", true)) {
									filename = field.value.substring(field.value
											.lastIndexOf('\\')
											+ 1);
								} else {
									filename = field.value.substring(field.value
											.lastIndexOf('/')
											+ 1);
								}
								field.ownerCt.getForm().submit({
									url: basePath + 'common/upload.action?em_code=' + em_code+'&caller='+caller,
									waitMsg: "正在上传:" + filename,
									success : function(fp, o) {
										if (o.result.error) {
											showError(o.result.error);
										} else {
											var store = new Array();
											var fpid = o.result.filepath+";";
											store.push({
												MF_ALDID:detailId,
												MF_CREATER:em_name,
												MF_CREATETIME:Ext.Date.format(new Date(),"Y-m-d H:i:s"),
												MF_FPID:fpid,
												MF_FILENAME:filename,
												MF_CALLER:caller
											})
											Ext.Ajax.request({
												url : basePath + 'pm/mould/uploadDetailFile.action',
												async: false,
												params:{
													params:JSON.stringify(store),
													caller:caller,
													code:codeValue,
													keyvalue:keyValue,
													keyField:keyField
												},
												callback : function(options,success,response){
													var localJson = new Ext.decode(response.responseText);
										   			if(localJson.exceptionInfo){
										   				var str = localJson.exceptionInfo;
										   				showError(str);return;
										   			}
									    			if(localJson.success){
									    				if(caller=='ProductSet'){
									    					detailId = view.ownerCt.getSelectionModel().getSelection()[0].data.psd_id;
									    				}
									    				if(caller=='Alter!Mould'){
									    					detailId = view.ownerCt.getSelectionModel().getSelection()[0].data.ald_id;
									    				}
									    				Ext.Ajax.request({
															url : basePath + 'common/getFieldsDatas.action',
															async: false,
															params:{
																fields : 'mf_id,mf_aldid,mf_version,mf_creater,mf_createtime,mf_fpid,mf_filename,mf_caller',
																caller : 'mod_fileversion',
																condition : "mf_aldid="+detailId+" and mf_caller='"+caller+"' order by mf_version"
															},
															callback : function(options,success,response){
																var rs = new Ext.decode(response.responseText);
																if(rs.exceptionInfo){
																	showError(rs.exceptionInfo);return;
																}
																var rs = Ext.decode(rs.data);
																if(rs.length>0){
																	var nowFields = new Array();
																	Ext.each(rs,function(r){
																		nowFields.push({
																			mf_id:r.MF_ID,
																			mf_aldid:r.MF_ALDID,
																			mf_version:r.MF_VERSION,
																			mf_creater:r.MF_CREATER,
																			mf_createtime:r.MF_CREATETIME,
																			mf_fpid:r.MF_FPID,
																			mf_filename:r.MF_FILENAME,
																			mf_caller:r.MF_CALLER
																	    });
																	});
																	Ext.getCmp('versionGrid').store.loadData(nowFields);
																}else{
																	Ext.getCmp('versionGrid').store.loadData(new Array());
																}
																showMessage('提示','上传成功',1000);
															}
														});
										   			}
												}
											});
										}
									},
									failure: function(fp, o){
										if (o.result.error) {
											showError(o.result.error);
										}
									}
								});
							}
						}
				  	}]
				},{

					xtype:'gridpanel',
					id:'versionGrid',
					width:'100%',
					height:'90%',				
					columns:[{
						text:'ID',
						dataIndex:'mf_id',
						width:0
					},{
						cls : "x-grid-header-1",
						header: '下载',
						xtype:'actioncolumn',		
						align:'center',
						width:40,
						icon: basePath + 'resource/images/icon/download.png',
						tooltip: '下载',
						handler: function(grid, rowIndex, colIndex) {	
							var select=grid.getStore().getAt(rowIndex);
							download(select.data.mf_fpid);
						}
					},{
						cls : "x-grid-header-1",
						text: '文件名称',
						dataIndex: 'mf_filename',
						width:300,
						readOnly:true				
					},{
						cls : "x-grid-header-1",
						text: '修订号',
						dataIndex: 'mf_version',
						width:60,
						align:'center',
						readOnly:true
					},{
						cls : "x-grid-header-1",
						text:'操作人',
						align:'center',
						dataIndex:'mf_creater',
						width:100,
						readOnly:true
					},{
						cls:'x-grid-header-1',
						text:'操作时间',
						dataIndex:'mf_createtime',
						width:170,
						readOnly:true,
						xtype:"datecolumn",
						format:"Y-m-d H:i:s"
					},{
						cls : "x-grid-header-1",
						header: '删除',
						xtype:'actioncolumn',		
						align:'center',
						width:40,
						icon: basePath + 'resource/images/icon/delete.png',
						tooltip: '删除',
						handler: function(grid, rowIndex, colIndex) {	
							var select=grid.getStore().getAt(rowIndex);
							Ext.Ajax.request({
								url : basePath + 'pm/mould/deleteDetailFile.action',
								async: false,
								params:{
									id:select.data.mf_id,
									caller:caller,
									code:codeValue,
									keyvalue:keyValue,
									keyField:keyField
								},
								callback : function(options,success,response){
									var rs = new Ext.decode(response.responseText);
									if(rs.exceptionInfo){
										showError(rs.exceptionInfo);return;
									}
									if(rs.success){
										if(caller=='ProductSet'){
											detailId = view.ownerCt.getSelectionModel().getSelection()[0].data.psd_id;
										}
										if(caller=='Alter!Mould'){
											detailId = view.ownerCt.getSelectionModel().getSelection()[0].data.ald_id;
										}
										Ext.Ajax.request({
											url : basePath + 'common/getFieldsDatas.action',
											async: false,
											params:{
												fields : 'mf_id,mf_aldid,mf_version,mf_creater,mf_createtime,mf_fpid,mf_filename,mf_caller',
												caller : 'mod_fileversion',
												condition : "mf_aldid="+detailId+" and mf_caller='"+caller+"' order by mf_version"
											},
											callback : function(options,success,response){
												var rs = new Ext.decode(response.responseText);
												if(rs.exceptionInfo){
													showError(rs.exceptionInfo);return;
												}
												var rs = Ext.decode(rs.data);
												if(rs.length>0){
													var nowFields = new Array();
													Ext.each(rs,function(r){
														nowFields.push({
															mf_id:r.MF_ID,
															mf_aldid:r.MF_ALDID,
															mf_version:r.MF_VERSION,
															mf_creater:r.MF_CREATER,
															mf_createtime:r.MF_CREATETIME,
															mf_fpid:r.MF_FPID,
															mf_filename:r.MF_FILENAME,
															mf_caller:r.MF_CALLER
													    });
													});
													Ext.getCmp('versionGrid').store.loadData(nowFields);
												}else{
													Ext.getCmp('versionGrid').store.loadData(new Array());
												}
											}
										});
										showMessage('提示','删除成功',1000);
									}
								}
							});
						}
					}],
					store:Ext.create('Ext.data.Store', {
						fields:[{
							name: 'mf_id',
							type: 'number'
						},{
							name:'mf_aldid',
							type:'number'
						},{
							name:'mf_version',
							type:'number'
						},{
							name: 'mf_creater',
							type: 'string'
						},{
							name:'mf_createtime',
							type:'date'
						},{
							name:'mf_fpid',
							type:'string'
						},{
							name:'mf_filename',
							type:'string'
						}],
						data:[]
					}),
					listeners:{
						afterrender:function(grid){
							var me = this;
							if(caller=='ProductSet'){
								detailId = view.ownerCt.getSelectionModel().lastSelected.data.psd_id;
							}
							if(caller=='Alter!Mould'){
								detailId = view.ownerCt.getSelectionModel().lastSelected.data.ald_id;
							}
							Ext.Ajax.request({
								url : basePath + 'common/getFieldsDatas.action',
								async: false,
								params:{
									fields : 'mf_id,mf_aldid,mf_version,mf_creater,mf_createtime,mf_fpid,mf_filename,mf_caller',
									caller : 'mod_fileversion',
									condition : "mf_aldid="+detailId+" and mf_caller='"+caller+"' order by mf_version"
								},
								callback : function(options,success,response){
									var rs = new Ext.decode(response.responseText);
									if(rs.exceptionInfo){
										showError(rs.exceptionInfo);return;
									}
									var rs = Ext.decode(rs.data);
									if(rs.length>0){
										var nowFields = new Array();
										Ext.each(rs,function(r){
											nowFields.push({
												mf_id:r.MF_ID,
												mf_aldid:r.MF_ALDID,
												mf_version:r.MF_VERSION,
												mf_creater:r.MF_CREATER,
												mf_createtime:r.MF_CREATETIME,
												mf_fpid:r.MF_FPID,
												mf_filename:r.MF_FILENAME,
												mf_caller:r.MF_CALLER
										    });
										});
										Ext.getCmp('versionGrid').store.loadData(nowFields);
									}else{
										Ext.getCmp('versionGrid').store.loadData(new Array());
									}
								}
							});
						}
					}
			  	}]
			});
			if(mainKeyValue==0){
				showError('请保存明细再上传附件！');
			}else{
				win.show();
			}
		};
		var mainId=0;
		if(caller=='ProductSet'){
			mainId = record.data.psd_id;
		}
		if(caller=='Alter!Mould'){
			mainId = record.data.ald_id;
		}
		return "<input type='button' value='管理附件' id='getProductFileDetails' style='color:gray;font-size:13px;cursor:pointer;height:25px;align:center;' onClick='updateLoadFileWindow("+mainId+")') />";
	},
});

