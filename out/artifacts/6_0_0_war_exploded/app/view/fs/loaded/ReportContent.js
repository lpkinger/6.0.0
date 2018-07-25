Ext.define('erp.view.fs.loaded.ReportContent',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit',
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype:'tabpanel',
				id:'reportcontent',
				title : '调查报告内容',
				items: [{ 
					title : '授信业务状况',
					tag : 'iframe',
					tabConfig:{tooltip:'授信业务状况'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_manage" src="'+basePath+'jsps/fs/loaded/creditCondition.jsp?gridCondition='+formCondition.replace('li_id','cd_liid')+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '客户基本情况检查',
					tag : 'iframe',
					id:'base',
					tabConfig:{tooltip:'客户基本情况检查'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_base" src="'+basePath+'jsps/fs/loaded/baseSituation.jsp?formCondition='+formCondition+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '客户经营检查',
					tag : 'iframe',
					id:'business',
					tabConfig:{tooltip:'客户经营检查'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_base" src="'+basePath+'jsps/fs/loaded/businessCheck.jsp?formCondition='+formCondition+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '财务状况检查',
					tag : 'iframe',
					id:'financial',
					tabConfig:{tooltip:'财务状况检查'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_base" src="'+basePath+'jsps/fs/loaded/financialCheck.jsp?formCondition='+formCondition+'&gridCondition='+formCondition.replace('li_id','lfi_liid')+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '买卖双方交易检查',
					tag : 'iframe',
					id:'transaction',
					tabConfig:{tooltip:'买卖双方交易检查'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_base" src="'+basePath+'jsps/fs/loaded/transactionCheck.jsp?formCondition='+formCondition+'&gridCondition='+formCondition.replace('li_id','lft_liid')+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '主要结算账户检查',
					tag : 'iframe',
					id:'settleaccount',
					tabConfig:{tooltip:'主要结算账户检查'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_base" src="'+basePath+'jsps/fs/loaded/settleAccountCheck.jsp?formCondition='+formCondition+'&gridCondition='+formCondition.replace('li_id','sta_liid')+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '信用情况检查',
					tag : 'iframe',
					id:'creditSituation',
					tabConfig:{tooltip:'信用情况检查'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_base" src="'+basePath+'jsps/fs/loaded/creditSituationCheck.jsp?formCondition='+formCondition+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '担保条件检查',
					tag : 'iframe',
					id:'guarantee',
					tabConfig:{tooltip:'担保条件检查'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_base" src="'+basePath+'jsps/fs/loaded/guaranteeCheck.jsp?formCondition='+formCondition+'&gridCondition='+formCondition.replace('li_id','lfm_liid')+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				},{ 
					title : '授信后检查结论',
					tag : 'iframe',
					id:'findings',
					tabConfig:{tooltip:'授信后检查结论'},
					border : false,
					layout : 'fit',
					html : '<iframe id="iframe_add_base" src="'+basePath+'jsps/fs/loaded/findings.jsp?formCondition='+formCondition+'&_noc=1" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>'
				}]
			}]
		}); 
		this.callParent(arguments); 
	}
});