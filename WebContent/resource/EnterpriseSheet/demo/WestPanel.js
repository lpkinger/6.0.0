/**
 * Enterprise Spreadsheet Solutions
 * Copyright(c) FeyaSoft Inc. All right reserved.
 * info@enterpriseSheet.com
 * http://www.enterpriseSheet.com
 * 
 * Licensed under the EnterpriseSheet Commercial License.
 * http://enterprisesheet.com/license.jsp
 * 
 * You need to have a valid license key to access this file.
 */
Ext.define('enterpriseSheet.demo.WestPanel', {
    extend : 'Ext.Panel',	
	region: 'west',
	split: true, 
	width:  250,
	minWidth: 100,
    border: false,
    style: 'border-right:1px solid silver;',
    collapsible: true,
   // collapsed:true,
    title: 'EnterpriseSheet Examples',
    layout: 'border',
    
    docUrl: 'http://enterprisesheet.com/api/',
    srcHtml: '',
	
	initComponent : function(){
		
		this.store = new Ext.data.Store({
			model: 'EnterpriseSheet.sheet.model.TargetModel',
			groupField: 'sheetName'
		});
		
		var useCases = [{
	    	iconCls: 'icon-feature',
	    	id: 'cases-feature',
	        text: 'EnterpriseSheet Features',
	        leaf: true
	    }, {
	    	iconCls: 'icon-money',
	    	id: 'cases-expense',
	        text: 'Hour and Expense Tracking',
	        leaf: true
	    }, {
	    	iconCls: 'icon-calendar',
	    	id: 'cases-calendar',
	        text: 'Calendar',
	        leaf: true
	    }, {
	    	iconCls: 'icon-cake',
	    	id: 'cases-wedding',
	        text: 'Wedding Budget',
	        leaf: true
	    }, /** {
	    	iconCls: 'icon-sun',
	    	id: 'cases-garden',
	        text: 'Garden Planner',
	        leaf: true
	    }, **/{
	    	iconCls: 'icon-chart',
	    	id: 'cases-weight',
	        text: 'His and her weight loss tracker',
	        leaf: true
	    }, {
			iconCls: 'icon-invoice',
	    	id: 'feature-special-dataBindingVariable1',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/customized/dataBindingVariable.html',
	    	exampleJson: 'dataBindingVariableJson',
	    	qtip: 'Right click open document',
	        text: 'Invoice (data binding)',
	        leaf: true
		}, {
			iconCls: 'icon-survey',
	    	id: 'feature-special-survey',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/customized/dynamicFormBuilder.html',
	    	exampleJson: 'surveyCaseJson',
	    	qtip: 'Right click open document',
	        text: 'EnterpriseSheet Survey (form builder)',
	        leaf: true
		}, {
			iconCls: 'icon-dataType',
	    	id: 'feature-col-datatype',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/colDataType.html',
	    	exampleJson: 'featureColDataTypeJson',
	        text: 'Define Column Data Types',
	        leaf: true
		}];
		
		// ===============================================================
		var conditionChild = [{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-condition-highlight',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/compareCond.html',
	    	exampleJson: 'featureConditionHighlightJson',
	    	qtip: 'Right click open document',
	        text: 'Number condition rule',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-condition-string',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/stringCompareCond.html',
	    	exampleJson: 'featureConditionStringJson',
	    	qtip: 'Right click open document',
	        text: 'Text condition rule',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-condition-date',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/dateCond.html',
	    	exampleJson: 'featureConditionDateJson',
	    	qtip: 'Right click open document',
	        text: 'Date occurring rule',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-condition-topBottom',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/calculateCond.html',
	    	exampleJson: 'featureConditionTopBottomJson',
	    	qtip: 'Right click open document',
	        text: 'Top/bottom rule',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-condition-bar',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/colorfulBarCond.html',
	    	exampleJson: 'featureConditionBarJson',
	    	qtip: 'Right click open document',
	        text: 'Data bar',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-condition-colorScales',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/colorScalesCond.html',
	    	exampleJson: 'featureConditionColorScalesJson',
	    	qtip: 'Right click open document',
	        text: 'Color scales',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-condition-iconset',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/iconSetsCond.html',
	    	exampleJson: 'featureConditionIconsetJson',
	    	qtip: 'Right click open document',
	        text: 'Icon sets',
	        leaf: true
	    }];
		
		var validationChild = [{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-validation-number',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/validationNum.html',
	    	exampleJson: 'featureValidationNumberJson',
	    	qtip: 'Right click open document',
	        text: 'Number validation',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-validation-text',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/validationText.html',
	    	exampleJson: 'featureValidationTextJson',
	    	qtip: 'Right click open document',
	        text: 'Text validation',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-validation-date',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/validationDate.html',
	    	exampleJson: 'featureValidationDateJson',
	    	qtip: 'Right click open document',
	        text: 'Date validation',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-validation-list',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/validationList.html',
	    	exampleJson: 'featureValidationListJson',
	    	qtip: 'Right click open document',
	        text: 'List item validation',
	        leaf: true
	    }];
		
		var chartChild = [{
			iconCls: 'icon-tag-purple',
	    	id: 'feature-chart-bar',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/chartColumn.html',
	    	exampleJson: 'featureChartBarJson',
	    	qtip: 'Right click open document',
	        text: 'Generate column / bar chart',
	        leaf: true
		}, {
			iconCls: 'icon-tag-purple',
	    	id: 'feature-chart-area',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/chartArea.html',
	    	exampleJson: 'featureChartAreaJson',
	        text: 'Generate area chart',
	        leaf: true
		}, {
			iconCls: 'icon-tag-purple',
	    	id: 'feature-chart-line',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/chartLine.html',
	    	exampleJson: 'featureChartLineJson',
	    	qtip: 'Right click open document',
	        text: 'Generate line / scatter chart',
	        leaf: true
		}, {
			iconCls: 'icon-tag-purple',
	    	id: 'feature-chart-pie',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/chartPie.html',
	    	exampleJson: 'featureChartPieJson',
	    	qtip: 'Right click open document',
	        text: 'Generate pie chart',
	        leaf: true
		}, {
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-sparkline-col',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/sparklineCol.html',
	    	exampleJson: 'featureSparklineJson',
	    	qtip: 'Right click open document',
	        text: 'Sparkline column chart',
	        leaf: true
	    }, {
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-sparkline-winloss',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/sparklineWinloss.html',
	    	exampleJson: 'featureSparklineWinLossJson',
	    	qtip: 'Right click open document',
	        text: 'Sparkline win/loss chart',
	        leaf: true
	    }, {
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-sparkline-line',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/sparklineLine.html',
	    	exampleJson: 'featureSparklineLineJson',
	    	qtip: 'Right click open document',
	        text: 'Sparkline line chart',
	        leaf: true
	    }];	
		
		var cellTypeChild = [{
			iconCls: 'icon-tag-purple',
	    	id: 'feature-cell-checkbox',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/checkbox.html',
	    	exampleJson: 'featureCheckboxJson',
	    	qtip: 'Right click open document',
	        text: 'Checkbox/Radio cell',
	        leaf: true
		}, {
			iconCls: 'icon-tag-purple',
	    	id: 'feature-cell-combobox',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/dropdown.html',
	    	exampleJson: 'featureComboboxJson',
	    	qtip: 'Right click open document',
	        text: 'Combobox cell',
	        leaf: true
		}, {
			iconCls: 'icon-tag-purple',
	    	id: 'feature-cell-link',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/cellLink.html',
	    	exampleJson: 'featureLinkJson',
	    	qtip: 'Right click open document',
	        text: 'Hyperlink cell',
	        leaf: true
		}, {
			iconCls: 'icon-tag-purple',
	    	id: 'feature-cell-button',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/button.html',
	    	exampleJson: 'featureButtonJson',
	    	qtip: 'Right click open document',
	        text: 'Button cell',
	        leaf: true
		}, {
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-special-comment',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/cellComment.html',
	    	exampleJson: 'featureCommentJson',
	    	qtip: 'Right click open document',
	        text: 'Comment cell',
	        leaf: true
	    }];
		
		var dataBindingChild = [{
			iconCls: 'icon-tag-green',
	    	id: 'feature-cellBinding',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/customized/customExtraData.html',
	    	exampleJson: 'callbackCellDataBindingJson',
	    //	exampleCode: 'callbackCellDataBindingCode',
	    	qtip: 'Right click open document',
	        text: 'Bind custom extra data to the cell',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'feature-2wayDataBinding',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/customized/customDataBinding.html',
	    	exampleJson: 'callback2wayDataBindingJson',
          //  exampleCode: 'callback2wayDataBindingCode',
	    	qtip: 'Right click open document',
	        text: '2-way cell data binding',
	        leaf: true
		}, {
			iconCls: 'icon-tag-green',
	    	id: 'feature-complexDataBinding1',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/customized/dataBindingSubmit.html',
	    	exampleJson: 'dataBindingSubmitJson',
	    	qtip: 'Right click open document',
	        text: 'Data binding and submit',
	        leaf: true
		}, {
			iconCls: 'icon-tag-green',
	    	id: 'feature-special-dataBindingVariable',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/customized/dataBindingVariable.html',
	    	exampleJson: 'dataBindingVariableJson',
	    	qtip: 'Right click open document',
	        text: 'Data binding with variable',
	        leaf: true
		}, {
			iconCls: 'icon-tag-green',
	    	id: 'feature-cellEventBinding',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/customized/dataBindingVariable.html',
	    	exampleJson: 'cellEventBindingJson',
	    	exampleCode: 'cellEventBindingCode',
	    	qtip: 'Right click open document',
	        text: 'Cell event binding',
	        leaf: true
        }];
		
		var dataFormatChild = [{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-money',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/money.html',
	    	exampleJson: 'featureMoneyJson',
	    	qtip: 'Right click open document',
	        text: 'Money format',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-dateFormat',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/dateFormat.html',
	    	exampleJson: 'featureDateJson',
	    	qtip: 'Right click open document',
	        text: 'Date format',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-customNum',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/customNumber.html',
	    	exampleJson: 'featureCustomJson',
	    	qtip: 'Right click open document',
	        text: 'Custom number format',
	        leaf: true
	    }];
		
		var formulaChild = [{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-formula',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/formulaCell.html',
	    	exampleJson: 'featureFormulaJson',
	    	qtip: 'Right click open document',
	        text: 'Formula',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-customizedFormula',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/addCalculates.html',
	    	exampleJson: 'featureCusomizedFormulaJson',
	        text: 'Add customized calculate function',
	        qtip: 'Right click open document',
	        leaf: true
	    }];
		
		var nameMgrChild = [{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-nmgr-init',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/nameMgr.html',
	    	exampleJson: 'featureFormulaNmgrJson',
	    	qtip: 'Right click open document',
	        text: 'Name manager init',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-nmgr-add',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/nameMgr.html',
	    	exampleJson: 'featureFormulaNmgrJsonAdd',
	    	qtip: 'Right click open document',
	        text: 'Name manager add',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-nmgr-del',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/nameMgr.html',
	    	exampleJson: 'featureFormulaNmgrJsonDel',
	    	qtip: 'Right click open document',
	        text: 'Name manager Del',
	        leaf: true
	    }];
		
		var titleBarChild = [{
		    iconCls: 'icon-tag-green',
	    	id: 'feature-titlebar-freeze',
	        text: 'Toggle freeze',
	        leaf: true	
		},{
		    iconCls: 'icon-tag-green',
	    	id: 'feature-titlebar-search',
	        text: 'Show find/replace window',
	        leaf: true	
		},{
		    iconCls: 'icon-tag-green',
	    	id: 'feature-titlebar-chart',
	        text: 'Show chart window',
	        leaf: true	
		},{
		    iconCls: 'icon-tag-green',
	    	id: 'feature-titlebar-table',
	        text: 'Show table style window',
	        leaf: true	
		},{
		    iconCls: 'icon-tag-green',
	    	id: 'feature-titlebar-cellStyle',
	        text: 'Show cell style window',
	        leaf: true	
		},{
		    iconCls: 'icon-tag-green',
	    	id: 'feature-titlebar-picture',
	        text: 'Show picture window',
	        leaf: true	
		},{
		    iconCls: 'icon-tag-green',
	    	id: 'feature-titlebar-widget',
	        text: 'Show widget window',
	        leaf: true	
		},{
		    iconCls: 'icon-tag-green',
	    	id: 'feature-titlebar-condition',
	        text: 'Show condition window',
	        leaf: true	
		},{
		    iconCls: 'icon-tag-green',
	    	id: 'feature-titlebar-toggleGridLine',
	        text: 'Hide gridline',
	        leaf: true	
		},{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-addPicture',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/insertImage.html',
	    	exampleJson: 'featureInsertPictureJson',
	    	qtip: 'Right click open document',
	        text: 'Insert image',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-addWidget',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/insertWedgit.html',
	    	exampleJson: 'featureInsertWidgetJson',
	    	qtip: 'Right click open document',
	        text: 'Insert a widget',
	        leaf: true
	    }];
		
		var textStyleChild = [{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-basic',
	    	exampleJson: 'featureBasicJson',
	        text: 'Basic Features',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-cellFont',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/cellFont.html',
	    	exampleJson: 'featureCellFontJson',
	    	qtip: 'Right click open document',
	        text: 'Text Decoration',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-cellAlign',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/cellAlign.html',
	    	exampleJson: 'featureCellAlignJson',
	    	qtip: 'Right click open document',
	        text: 'Text align and indent',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-textColor',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/cellColor.html',
	    	exampleJson: 'featureCellColorJson',
	    	qtip: 'Right click open document',
	        text: 'Set cell color',
	        leaf: true
	    }];
		
		var tabActionChild = [{
	    	iconCls: 'icon-tag-green',
		    id: 'feature-default-sheet-style',
		    docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/sheetStyle.html',
		    exampleJson: 'sheetDefaultStyle',
	    	qtip: 'Right click open document',
	        text: 'Set sheet default style',
	        leaf: true
	    }, {
	    	iconCls: 'icon-tag-green',
		    id: 'feature-sheet-group',
		    docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/groupSheet.html',
	    	exampleJson: 'featureGroup',
	    	qtip: 'Right click open document',
	        text: 'Group range',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-sheet-freeze',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#toggleFreeze',
	    	exampleJson: 'featureFreezeSheet',
	        text: 'Freezing (cell B2)',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-autoScroll',
	    	exampleJson: 'featureAutoScroll',
	        text: 'Scroll bar',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-sheet-add',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/addTab.html',
	    	exampleJson: 'featureAddSheet',
	        text: 'Add new sheet',
	        leaf: true
	    }, {
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-sheet-retrieve',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/getTabData.html',
	    	exampleJson: 'featureRetrieveSheet',
	        text: 'Retrieve sheet tab data',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-disableSheet',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/readOnly.html',
	    	exampleJson: 'featureDisableSheetJson',
	    	qtip: 'Right click open document',
	        text: 'Disable Sheet',
	        leaf: true
	    },];
		
		var performanceChild = [{
			iconCls: 'icon-tag-purple',
	    	id: 'performance-render-10000',
	        text: 'Render 10,000 data',
	        leaf: true
		},{
			iconCls: 'icon-tag-purple',
	    	id: 'performance-render-50000',
	        text: 'Render 50,000 data',
	        leaf: true
		},{
			iconCls: 'icon-tag-purple',
	    	id: 'performance-render-100000',
	        text: 'Render 100,000 data',
	        leaf: true
		},{
			iconCls: 'icon-tag-purple',
	    	id: 'performance-render-60-200',
	        text: 'Render 60*200 data',
	        leaf: true
		},{
			iconCls: 'icon-tag-purple',
	    	id: 'performance-render-3tabs',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/customized/loadDataSwitchTab.html',
	        text: 'load data during switch sheet tab',
	        exampleJson: 'loadDataSwitchTabJson',
	        leaf: true
		}];
		
		// ============================================================
		
		var rowColActionChild = [{
			iconCls: 'icon-tag-green',
	    	id: 'feature-rowCol-hide',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/rowColsHide.html',
	    	exampleJson: 'featureRowColHideJson',
	    	qtip: 'Right click open document',
	        text: 'Hide rows/columns',
	        leaf: true
		},{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-disable',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/disable.html',
	    	exampleJson: 'featureDisableJson',
	    	qtip: 'Right click open document',
	        text: 'Disable cell/row/column',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-rowCol-heighCol',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/rowColWidth.html',
	    	exampleJson: 'featureRowColColorJson',
	    	qtip: 'Right click open document',
	        text: 'Set row/column height, color',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-special-addNewRow',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/insertRows.html',
	    	exampleJson: 'featureAddRowJson',
	    	qtip: 'Right click open document',
	        text: 'Add new row to sheet',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-special-addNewCol',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/insertCols.html',
	    	exampleJson: 'featureAddColJson',
	    	qtip: 'Right click open document',
	        text: 'Add new column to sheet',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'feature-special-setrowcolnumber',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/setMaxRowCol.html',
	    	exampleJson: 'featureMaxColRowJson',
	    	qtip: 'Right click open document',
	        text: 'Set max row/column number',
	        leaf: true
	    }];
		
		// =============================================================
		
		var applyTableBorderChild = [{
			iconCls: 'icon-tag-green',
	    	id: 'feature-special-table',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/tableTpl.html',
	    	exampleJson: 'featureTableJson',
	    	qtip: 'Right click open document',
	        text: 'Apply table template to cell range',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'feature-sheet-applyBorder',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/codeExample/borderStyle.html',
	    	exampleJson: 'featureApplyBorderJson',
	    	qtip: 'Right click open document',
	        text: 'Apply border to cell range',
	        leaf: true
		}];
		
		// ==============================================================
		
		var filterSortChild = [{
			iconCls: 'icon-tag-green',
	    	id: 'feature-special-filter',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#addFilter2Span',
	    	exampleJson: 'filterCellsJson',
	    	qtip: 'Right click open document',
	        text: 'Add filter to the cell range',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'feature-special-tabs-filter',
	    	exampleJson: 'filterTabsJson',
	    	qtip: 'Right click open document',
	        text: 'Remove filter during switch tab',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'feature-sortitem',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#sortCellByAsc',
	    	exampleJson: 'sortCellsJson',
	    	qtip: 'Right click open document',
	        text: 'Add sort to the cell range',
	        leaf: true
		}];
		
		// ===============================================================
		
		var features = [{
	    	text: 'Apply table, border to cells', 
		    cls: 'folder',
		    expanded: false,
		    children: applyTableBorderChild
	    },{
	    	text: 'Cell type', 
		    cls: 'folder',
		    expanded: false,
		    children: cellTypeChild
	    },{
	    	text: 'Chart features/APIs', 
		    cls: 'folder',
		    expanded: false,
		    children: chartChild
	    },{
	    	text: 'Condition format', 
		    cls: 'folder',
		    expanded: false,
		    children: conditionChild
	    },{
	    	text: 'Data binding', 
		    cls: 'folder',
		    expanded: false,
		    children: dataBindingChild
	    },{
	    	text: 'Data format', 
		    cls: 'folder',
		    expanded: false,
		    children: dataFormatChild
	    },{
	    	text: 'Filter, sort cells', 
		    cls: 'folder',
		    expanded: false,
		    children: filterSortChild
	    },{
	    	text: 'Formula', 
		    cls: 'folder',
		    expanded: false,
		    children: formulaChild 
	    },{
	    	text: 'Name management', 
		    cls: 'folder',
		    expanded: false,
		    children: nameMgrChild
	    },{
	    	text: 'Performance', 
		    cls: 'folder',
		    expanded: false,
		    children: performanceChild
	    },{
	    	text: 'Row/Column actions', 
		    cls: 'folder',
		    expanded: false,
		    children: rowColActionChild
	    },{
	    	text: 'Sheet/Tab actions', 
		    cls: 'folder',
		    expanded: false,
		    children: tabActionChild
	    },{
	    	text: 'Text style', 
		    cls: 'folder',
		    expanded: false,
		    children: textStyleChild
	    },{
	    	text: 'Title bar related actions', 
		    cls: 'folder',
		    expanded: false,
		    children: titleBarChild
	    },{
	    	text: 'Validation', 
		    cls: 'folder',
		    expanded: false,
		    children: validationChild
	    }];
		
		// ====================================================
		// This is for API test
		// ====================================================
		var editMenuChild = [{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-format-bold',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#bold',
	        text: 'Add bold',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-format-italic',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#italic',
	        text: 'Add Italic',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-format-underline',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#underline',
	        text: 'Add underline',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-format-strikeline',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#strikeline',
	        text: 'Add strikeline',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-format-fontFamily',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#fontFamily',
	        text: 'Set font-family',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-format-fontSize',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#fontSize',
	        text: 'Set font-size',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-format-incFontSize',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#incFontSize',
	        text: 'Set incFontSize',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-format-desFontSize',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#desFontSize',
	        text: 'Set desFontSize',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-format-brush',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#italic',
	        text: 'Format brush',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-format-bgcColor',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#fillBackgroundColor',
	        text: 'Set background color',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-format-fontColor',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#fontColor',
	        text: 'Set font color',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-undo',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#undo',
	        text: 'Undo',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-redo',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#redo',
	        text: 'Redo',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-resetHistory',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#resetHistory',
	        text: 'Reset history',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-getAllHistory',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#getAllChanges',
	        text: 'Get all history',
	        leaf: true
	    }, {
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-purgeChangeList',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#purgeChangeList',
	        text: 'Purge the changed list 5 steps',
	        leaf: true
	    },  {
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-redoChangeList',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#redoChange',
	        text: 'Redo the changed list 5 steps',
	        leaf: true
	    },  { 
	    	iconCls: 'icon-tag-green',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#cut',
	    	id: 'testAPI-action-cut',
	        text: 'Cut',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-copy',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#copy',
	        text: 'Copy',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-paste',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#paste',
	        text: 'Paste',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-alignSet',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#alignSet',
	        text: 'Set align',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-border-set',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#setRangeBorder',
	        text: 'Set border',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-wordWrap',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#wordWrap',
	        text: 'WordWrap',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-rotate-text',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#rotateText',
	        text: 'Rotate text',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-merge-cell',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#mergeCell',
	        text: 'Merge cell',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-merge-column',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#mergeCellInColumn',
	        text: 'Merge column',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-merge-row',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#mergeCellInRow',
	        text: 'Merge row',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-cancel-merge',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#cancelMergeCell',
	        text: 'Cancel merge',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-create-merge',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#createMergeCell',
	        text: 'Create merge',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-get-merge',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#getMergeCell',
	        text: 'Get merge',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-delete-merge',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#deleteMergeCell',
	        text: 'Delete merge',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-all-merge',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#allMergeCell',
	        text: 'Get all merge',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-all-merge-head',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#allMergeCellHead',
	        text: 'Get all merge head',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-is-merge',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#isMergeCell',
	        text: 'Is merge cell',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-is-merge-head',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#isMergeCellHead',
	        text: 'Is merge cell head',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-move-dot',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#moveDecimalPoint',
	        text: 'Move decimal position',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-currency-format',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#currencyFormat',
	        text: 'Currency format',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-currency-format-win',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#currencyFormatWin',
	        text: 'Currency format win',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-locale-format',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#localeFormat',
	        text: 'Locale format',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-percent-format',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#percentFormat',
	        text: 'Percent format',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-comma-format',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#commaFormat',
	        text: 'Comma format',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-science-format',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#scienceFormat',
	        text: 'Science format',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-date-format',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#dateFormat',
	        text: 'Date format',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-time-format',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#timeFormat',
	        text: 'Time format',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-datetime-format',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#dateTimeFormat',
	        text: 'Datetime format',
	        leaf: true
	    }];
		
		// this is for insert 
		var insertMenuChild = [{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-picture-panel',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#showSidebarBtnWin',
	        text: 'Picture',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-chart-panel',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#showSidebarBtnWin',
	        text: 'Chart',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-findMatch',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#showSidebarBtnWin',
	        text: 'Find && replace',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-table-format',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#showSidebarBtnWin',
	        text: 'Table',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-cell-format',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#showSidebarBtnWin',
	        text: 'Cell',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-condition-format',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#showSidebarBtnWin',
	        text: 'Condition',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-background-panel',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertBackgroundImage',
	        text: 'Insert background image',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-insert-page-break',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertPageBreak',
	        text: 'Insert page break',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-delete-page-break',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#deletePageBreak',
	        text: 'Delete page break',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-insert-comment',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertComment',
	        text: 'Insert comment',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-insert-comment2',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertComment',
	        text: 'Insert comment2',
	        leaf: true
	    },{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-dropList',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertDropList',
	        text: 'Insert drop list',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-checkbox',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertCheckbox',
	        text: 'Insert checkbox',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-radio',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertRadio',
	        text: 'Insert radio',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-datepicker',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertDatePicker',
	        text: 'Insert date picker',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-clear-item',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#clearItem',
	        text: 'Clear item',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-name-range',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#nameRange',
	        text: 'Insert name range',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-name-range-update-address',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#nameRange',
	        text: 'Update name range address',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-name-range-update-comment',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#nameRange',
	        text: 'Update name range comment',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-insert-hyperlink',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertLink',
	        text: 'Insert hyperlink',
	        leaf: true
		}];
		
		var conditionMenuChild = [{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-get-style',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Get styles',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-get-date-option',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Get Date options',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-get-repeat',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Get repeat',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-greater-condition',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Greater condition',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-less-condition',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Less condition',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-equal-condition',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Equal condition',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-between-condition',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Between condition',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-include-condition',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Include condition',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-date-condition',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Date condition',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-repeat-condition',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Repeat condition',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-max-condition',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Top 10 condition',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-top-condition',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Top 10% condition',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-min-condition',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Bottom 10 condition',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-bottom-condition',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Bottom 10% condition',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-above-condition',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Above condition',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-below-condition',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Below condition',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-colorbar-condition',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Color bar condition',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-colorgrad-condition',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Color change condition',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-iconset-condition',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Iconset condition',
	        leaf: true
	    }];
	    
	    var searchMenuChild = [{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-find-text',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#max',
	        text: 'Find text',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-prev-text',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#min',
	        text: 'Prev',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-next-text',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#min',
	        text: 'Next',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-replace-select',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#min',
	        text: 'Replace Select',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-replace-all',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#min',
	        text: 'Replace All',
	        leaf: true
	    }];
	    
	    var pictureMenuChild = [{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-insert-picture',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#min',
	        text: 'Insert Picture',
	        leaf: true
	    }];
		
		// this is for formula 
		var formulasMenuChild = [{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-max',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#max',
	        text: 'Max',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-min',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#min',
	        text: 'Min',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-action-insert-function',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Insert function',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-refresh-formula-function',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Refresh Formula',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-mathmatics-function',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Mathmatics Functions',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-logic-function',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Logic Formula',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-lookup-function',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Lookup Formula',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-statistical-function',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Statistical Formula',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-engineering-function',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Engineering Formula',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-compatibility-function',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Compatibility Formula',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-finicial-function',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Financial Formula',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-string-function',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'String Formula',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-date-function',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Date Formula',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'testAPI-action-info-function',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#insertFormula',
	        text: 'Info Formula',
	        leaf: true
	    }];
		
		// this is for data menu 
		var dataMenuChild = [{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-data-validation',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#validation',
	        text: 'Validation',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-data-sort-asc',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#sortCellByAsc',
	        text: 'Sort asc',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-data-sort-desc',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#sortCellByDesc',
	        text: 'Sort Desc',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-data-filter',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#filter',
	        text: 'Filter',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-data-delete-repeat',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#deleteRepeat',
	        text: 'Delete repeat',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-data-clean',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#clean',
	        text: 'Clean',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-data-clean-content',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#cleanContent',
	        text: 'Clean content',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-data-clean-style',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#cleanStyle',
	        text: 'Clean style',
	        leaf: true
		}];
		
		var viewMenuChild = [{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-view-freeze',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#toggleFreeze',
	        text: 'Freeze',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-view-split',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#toggleSplit',
	        text: 'Toggle split',
	        leaf: true
		},{
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-view-toggleColumn',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#toggleColumn',
	        text: 'Toggle column name',
	        leaf: true
		}, {
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-view-toggleRow',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#toggleRow',
	        text: 'Toggle row name',
	        leaf: true
		}, {
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-view-toggleGrid',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#toggleGridLine',
	        text: 'Hide grid line',
	        leaf: true
		}, {
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-view-zoom',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#zoom',
	        text: 'Zoom',
	        leaf: true
		}, {
			iconCls: 'icon-tag-green',
	    	id: 'testAPI-view-lock-all',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/sheetAPI.html#toggleEditable',
	        text: 'Toggle lock',
	        leaf: true
		}];
		
		var generalChild = [{
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-general-getJsonData',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/getJsonData.html',
	        text: 'getJsonData',
	        leaf: true
	    }, {
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-general-getAllRangeMeTreeRefered',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/getJsonData.html',
	        text: 'getAllRangeMeTreeRefered',
	        leaf: true
	    }, {
	    	iconCls: 'icon-tag-green',
	    	id: 'testAPI-general-crossFileRef',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/crossFileRef.html',
	        text: 'cross File Ref Demo',
	        leaf: true
	    }];
		
		var bpDynamicRangeChild = [{
			iconCls: 'icon-tag-purple',
	    	id: 'feature-dynamicRange',	    		    	
	        text: 'dynamic Range',
	        docUrl: 'http://www.enterprisesheet.com/api/docs/manageDataAPIs/loadDataJson.html',
	        exampleJson: 'dynamicRangeJson',
	        leaf: true
	    }];
		
		var testAPIs = [{
	    	text: 'Edit', 
		    cls: 'folder',
		    expanded: false,
		    children: editMenuChild
	    }, {
	    	text: 'Insert', 
		    cls: 'folder',
		    expanded: false,
		    children: insertMenuChild
	    }, {
	    	text: 'Search', 
		    cls: 'folder',
		    expanded: false,
		    children: searchMenuChild
	    }, {
	    	text: 'Picture', 
		    cls: 'folder',
		    expanded: false,
		    children: pictureMenuChild
	    }, {
	    	text: 'Formulas', 
		    cls: 'folder',
		    expanded: false,
		    children: formulasMenuChild
	    }, {
	    	text: 'Condtion', 
		    cls: 'folder',
		    expanded: false,
		    children: conditionMenuChild
	    },  {
	    	text: 'Data', 
		    cls: 'folder',
		    expanded: false,
		    children: dataMenuChild
	    },  {
	    	text: 'View', 
		    cls: 'folder',
		    expanded: false,
		    children: viewMenuChild
	    }, {
	    	text: 'General APIs',
	    	cls: 'folder',
		    expanded: false,
		    children: generalChild
	    }];
		
		// ====================================================
		// ====================================================
		
		var callbackFn = [{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-groupToggle',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/customized/eventListenerFn.html',
	    	exampleJson: 'groupToggleEventListenerJson',
	    	exampleCode: 'groupToggleEventListenerCode',
	    	qtip: 'Right click open document',
	        text: 'Group event listener Fn',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-oncellBLUR',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/customized/cellEventCallbackFn.html',
	    	exampleJson: 'callbackCellBlurJson',
	    	exampleCode: 'callbackCellBlurCode',
	    	qtip: 'Right click open document',
	        text: 'Cell onBlur call back',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-oncellFocus',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/customized/cellEventCallbackFn.html',
	    	exampleJson: 'callbackCellFocusJson',
	    	qtip: 'Right click open document',
	        text: 'Cell onFocus call back',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-oncellclick',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/customized/cellEventCallbackFn.html',
	    	exampleJson: 'callbackCellClickJson',
	    	qtip: 'Right click open document',
	        text: 'Cell onClick call back',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-ondoubleclick',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/customized/cellEventCallbackFn.html',
	    	exampleJson: 'callbackCellDblClickJson',
	    	qtip: 'Right click open document',
	        text: 'Cell onDoubleClick call back',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-onmousemove',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/customized/cellEventCallbackFn.html',
	    	exampleJson: 'callbackMouseMoveJson',
	    	qtip: 'Right click open document',
	        text: 'Cell onMouseMove call back',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-onmouseDown',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/customized/cellEventCallbackFn.html',
	    	exampleJson: 'callbackMouseDownJson',
	    	qtip: 'Right click open document',
	        text: 'Cell onMouseDown call back',
	        leaf: true
	    },{
			iconCls: 'icon-tag-purple',
	    	id: 'feature-complexDataBinding1',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/customized/dataBindingSubmit.html',
	    	exampleJson: 'dataBindingSubmitJson',
	    	qtip: 'Right click open document',
	        text: 'Data binding and submit',
	        leaf: true
		},{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-onTabClick',
	    	docUrl: 'http://www.enterprisesheet.com/api/docs/customized/switchTabCallbackFn.html',
	    	exampleJson: 'callbackSheetSwitchJson',
	    	exampleCode: 'callbackSheetSwitchCode',
	    	qtip: 'Right click open document',
	        text: 'Switch tab event callback Fn',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-onCopyPaste',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/customized/switchTabCallbackFn.html',
	    	exampleJson: 'callbackCopyPasteJson',
	    	exampleCode: 'callbackCopyPasteCode',
	    	qtip: 'Right click open document',
	        text: 'Copy paste event callback Fn',
	        leaf: true
	    },{
	    	iconCls: 'icon-tag-purple',
	    	id: 'feature-afterCellChange',
	    	//docUrl: 'http://www.enterprisesheet.com/api/docs/customized/switchTabCallbackFn.html',
	    	exampleJson: 'callbackAfterCellChangeJson',
	    	exampleCode: 'callbackAfterCellChangeCode',
	    	qtip: 'Right click open document',
	        text: 'aftercellchange event callback Fn',
	        leaf: true
        }, {
            iconCls: 'icon-tag-green',
            id: 'feature-getDataFromRange',
            //docUrl: 'http://www.enterprisesheet.com/api/docs/customized/dataBindingSubmit.html',
            exampleJson: 'getDataFromRangeJson',
            qtip: 'Right click open document',
            text: 'Get data from range',
            leaf: true
        }];
		
		// =====================================================
		
		var restWebService = [{
			iconCls: 'icon-tag-red',
		    id: 'api-restWebService-import',
	        text: 'REST web service Import XLSX',
	        leaf: true
		}];

		// ====================================================== main children
		
		var children = [];
		/**
		if (!SCONFIG.js_standalone) {
		    children = [{ text: 'EnterpriseSheet Use Cases', cls: 'folder', expanded: true, children: useCases}];	
		}		
		**/
		children.push({ text: 'EnterpriseSheet Samples / APIs',  cls: 'folder', expanded: false, children: features });	
		children.push({ text: 'Test APIs',  cls: 'folder', expanded: true, children: testAPIs });	
		children.push({ text: 'Event listener & customer callback fn',  cls: 'folder', expanded: false, children: callbackFn });	
		if (!SCONFIG.js_standalone) {
			children.push({ text: 'REST web service Samples', cls: 'folder', expanded: false, children: restWebService});	
		}

		children.push({ iconCls: 'icon-help', id: 'link-sheet-document', text: 'EnterpriseSheet Documents', leaf: true });
		
		// ==================================================================
		
		this.treeStore = Ext.create('Ext.data.TreeStore', {
            root: {
	            expanded: true,
	            children: children
	        }
	    });
		
		this.demoTree = Ext.create('Ext.tree.Panel', { 
			region: 'center',
			border: false,
	        useArrows:true,
	        autoScroll:true,
	        animate:true,
	        enableDD: false,
	        containerScroll: false,
	        rootVisible: false,
	        store: this.treeStore,
	        viewConfig:{
		        cls:'large-font'
		    },
	    });
		
		this.treeMenu = new Ext.menu.Menu({
	        items: [{
	            text: 'Detail documents',
	            handler: function() {
	                window.open(this.docUrl,'_blank');
	            },
	            scope: this
	        }, {
	            text: 'Json data',
	            handler: function() {
	                var sourceWin = Ext.create('enterpriseSheet.demo.SourceWin', {srcHtml: this.srcHtml});
	                sourceWin.show();
	            },
	            scope: this
	        }],
	        scope: this
	    });
		
		this.treeMenu2 = new Ext.menu.Menu({
	        items: [{
	            text: 'Detail documents',
	            handler: function() {
	                window.open(this.docUrl,'_blank');
	            },
	            scope: this
	        }],
	        scope: this
	    });
		
		
    	this.items = [{
    		xtype: 'component',
            region:"south",
            autoHeight:true,                       
            html: '<div class="x-view-emtpytext">Open tree node contextmenu to see detail document and source</div>'
        }, this.demoTree];
    	
        this.callParent();
        
		this.demoTree.on("itemclick", this.onClickHandler, this);		
		this.demoTree.on('itemcontextmenu', this.onContextMenu, this);	
		
	},
	
	selectNode : function() {
		var query = window.location.search.substring(1);
		var pair = query.split("=");
		if (pair[0] == "loadChildId") {
			var node = this.treeStore.getNodeById(pair[1]);
			this.demoTree.getSelectionModel().select(node);
			if (pair[1] == "feature-special-survey") {
				this.centralPanel.specialHandler(node.data.id, node.data.text, "surveyCaseJson");
			}
		}
	},
	
	onClickHandler : function(view, record, obj, options) {
		
		var itemId = record.data.id, titleTxt = record.data.text,  exampleJson = record.raw.exampleJson, 
		    exampleCode = record.raw.exampleCode;
		
		if (itemId && itemId.lastIndexOf("cases-", 0) === 0) {
			this.centralPanel.loadUserCases(itemId, titleTxt);
		}
		
		else if (itemId && itemId.lastIndexOf("link-sheet-", 0) === 0) {
			this.centralPanel.outLink(itemId);
		}
		
		else if (itemId && itemId.lastIndexOf("performance-", 0) === 0) {
			if (itemId == "performance-render-3tabs") 
				this.centralPanel.performanceSheetTab(itemId, titleTxt);
			else 
		        this.centralPanel.performanceSheet(itemId, titleTxt);
		}
		
		else if (itemId && itemId.lastIndexOf("feature-titlebar-") === 0) {
			this.centralPanel.titlebarAction(itemId, titleTxt);
		}
		
		else if (itemId && itemId.lastIndexOf("feature-sheet-") === 0) {
			this.centralPanel.sheetTabHandler(itemId, titleTxt, exampleJson);
		} 
		
		else if (itemId && itemId.lastIndexOf("api-restWebService-") === 0) {
			this.centralPanel.restApiHandler(itemId);
		}

        else if (itemId && itemId.lastIndexOf("feature-customizedFormula") === 0) {
			this.centralPanel.customizedFormula(itemId, titleTxt, exampleJson);
		}
		
		else if (itemId && itemId.lastIndexOf("feature-special-") === 0) {
			this.centralPanel.specialHandler(itemId, titleTxt, exampleJson);
		}
		
		else if (itemId && itemId.lastIndexOf("feature-col-datatype") === 0) {
			this.centralPanel.colDataTypeHandler(itemId, titleTxt, exampleJson);
		}
		
        else if (itemId && itemId.lastIndexOf("feature-nmgr-") === 0) {
			if (itemId.lastIndexOf("feature-nmgr-init") === 0) {
        	    this.centralPanel.handleNameMgr(itemId, titleTxt, exampleJson);
			} else if (itemId.lastIndexOf("feature-nmgr-add") === 0) {
        	    this.centralPanel.handleNameMgrAdd(itemId, titleTxt, exampleJson);
			} else if (itemId.lastIndexOf("feature-nmgr-del") === 0) {
        	    this.centralPanel.handleNameMgrDel();
			}
		}
		
		////////////////////////////////////////////////////////////////////////////////
		// add example for test API in here ...........................................
		////////////////////////////////////////////////////////////////////////////////
        else if (itemId && itemId.lastIndexOf("testAPI-", 0) === 0) {
			// ok, this is for TEST bold API ... user need select one of cell and call this function
			if (itemId == "testAPI-format-bold") {
				//alert("Please select one of cell first before you click this node");
				SHEET_API.bold(SHEET_API_HD);
			} else if (itemId == "testAPI-format-italic") {
				//alert("Please select one of cell first before you click this node");
				SHEET_API.italic(SHEET_API_HD);
			} else if (itemId == "testAPI-format-underline") {
				//alert("Please select one of cell first before you click this node");
				SHEET_API.underline(SHEET_API_HD);
			} else if (itemId == "testAPI-format-strikeline") {
				//alert("Please select one of cell first before you click this node");
				SHEET_API.strikeline(SHEET_API_HD);
			} else if (itemId == "testAPI-format-fontFamily") {
				//alert("Please select one of cell first before you click this node");
				SHEET_API.fontFamily(SHEET_API_HD, "Times New Roman");
			} else if (itemId == "testAPI-format-fontSize") {
				//alert("Please select one of cell first before you click this node");
				SHEET_API.fontSize(SHEET_API_HD, "10");
			} else if (itemId == "testAPI-format-incFontSize") {
				//alert("Please select one of cell first before you click this node");
				SHEET_API.incFontSize(SHEET_API_HD);
			} else if (itemId == "testAPI-format-desFontSize") {
				//alert("Please select one of cell first before you click this node");
				SHEET_API.desFontSize(SHEET_API_HD);
			} else if (itemId == "testAPI-format-desFontSize") {
				//alert("Please select one of cell first before you click this node");
				SHEET_API.desFontSize(SHEET_API_HD);
			} else if (itemId == "testAPI-format-brush") {
				//alert("Please select one of cell first before you click this node");
				SHEET_API.formatBrush(SHEET_API_HD);
			} else if (itemId == "testAPI-format-fontColor") {
				//alert("Please select one of cell first before you click this node");
				SHEET_API.fontColor(SHEET_API_HD, '#FF00FF');
			}      
						
			else if (itemId == "testAPI-action-undo") {
				SHEET_API.undo(SHEET_API_HD);
			} else if (itemId == "testAPI-action-redo") {
				SHEET_API.redo(SHEET_API_HD);
			} else if (itemId == "testAPI-action-resetHistory") {
				SHEET_API.resetHistory(SHEET_API_HD);
			} else if (itemId == "testAPI-action-getAllHistory") {
				alert("please see console for detail changed");
				var stack = SHEET_API.getAllChanges(SHEET_API_HD);
				console.log(stack);
			} else if (itemId == "testAPI-action-purgeChangeList") {
				alert("Undo 5 times");
				SHEET_API.purgeChangeList(SHEET_API_HD, 5);
			} else if (itemId == "testAPI-action-redoChangeList") {
				alert("Redo 5 times");
				SHEET_API.redoChange(SHEET_API_HD, 5);
			} 		
			
			else if (itemId == "testAPI-action-cut") {
				SHEET_API.cut(SHEET_API_HD);
			} else if (itemId == "testAPI-action-copy") {
				SHEET_API.copy(SHEET_API_HD);
			} else if (itemId == "testAPI-action-paste") {
				SHEET_API.paste(SHEET_API_HD);
			} else if (itemId == "testAPI-action-alignSet") {
				SHEET_API.alignSet(SHEET_API_HD, "bottom");
			} else if (itemId == "testAPI-action-border-set") {
				SHEET_API.setRangeBorder(SHEET_API_HD);
			} else if (itemId == "testAPI-action-wordWrap") {
				SHEET_API.wordWrap(SHEET_API_HD);
			} else if (itemId == "testAPI-action-rotate-text") {
				SHEET_API.rotateText(SHEET_API_HD, "45");
			} else if (itemId == "testAPI-action-merge-cell") {
				SHEET_API.mergeCell(SHEET_API_HD);
			} else if (itemId == "testAPI-action-merge-column") {
				SHEET_API.mergeCellInColumn(SHEET_API_HD);
			} else if (itemId == "testAPI-action-merge-row") {
				SHEET_API.mergeCellInRow(SHEET_API_HD);
			} else if (itemId == "testAPI-action-cancel-merge") {
				SHEET_API.cancelMergeCell(SHEET_API_HD);
			} else if (itemId == "testAPI-action-create-merge") {
				var mergeobj = [{sheet:0,range:[1,3,2,6]}];
				SHEET_API.createMergedRegion(SHEET_API_HD, mergeobj);
			} else if (itemId == "testAPI-action-get-merge") {
				SHEET_API.getMergedRegion(SHEET_API_HD, '0$1$3$2$6');
			} else if (itemId == "testAPI-action-delete-merge") {
				SHEET_API.deleteMergedRegion(SHEET_API_HD);
			} else if (itemId == "testAPI-action-all-merge") {
				SHEET_API.getAllMergedRegions(SHEET_API_HD);
			} else if (itemId == "testAPI-action-all-merge-head") {
				SHEET_API.getMergedRegionHeadCell(SHEET_API_HD);
			} else if (itemId == "testAPI-action-is-merge") {
				SHEET_API.isMergedCell(SHEET_API_HD);
			} else if (itemId == "testAPI-action-is-merge-head") {
				SHEET_API.isMergeHeadCell(SHEET_API_HD, 0, 1, 3);
			} else if (itemId == "testAPI-action-move-dot") {
				SHEET_API.moveDecimalPoint(SHEET_API_HD, 1);
			} else if (itemId == "testAPI-action-currency-format") {
				SHEET_API.currencyFormat(SHEET_API_HD, "rmb");
			} else if (itemId == "testAPI-action-currency-format-win") {
				SHEET_API.currencyFormatWin(SHEET_API_HD, "rmb");
			} else if (itemId == "testAPI-action-locale-format") {
				var options = { style: 'currency', currency: 'EUR', minimumFractionDigits: 4 };
				SHEET_API.localeFormat(SHEET_API_HD, "de-DE", options);
			} else if (itemId == "testAPI-action-percent-format") {
				SHEET_API.percentFormat(SHEET_API_HD);
			} else if (itemId == "testAPI-action-comma-format") {
				SHEET_API.commaFormat(SHEET_API_HD);
			} else if (itemId == "testAPI-action-science-format") {
				SHEET_API.scienceFormat(SHEET_API_HD);
			} else if (itemId == "testAPI-action-date-format") {
				SHEET_API.dateFormat(SHEET_API_HD);
			} else if (itemId == "testAPI-action-time-format") {
				SHEET_API.timeFormat(SHEET_API_HD);
			} else if (itemId == "testAPI-action-datetime-format") {
				SHEET_API.dateTimeFormat(SHEET_API_HD);
			} else if (itemId == "testAPI-action-findMatch") {
				SHEET_API.showSidebarBtnWin(SHEET_API_HD, "search");
			} else if (itemId == "testAPI-action-table-format") {
				SHEET_API.showSidebarBtnWin(SHEET_API_HD, "tableStyle");
			} else if (itemId == "testAPI-action-cell-format") {
				SHEET_API.showSidebarBtnWin(SHEET_API_HD, "cellStyle");
			} else if (itemId == "testAPI-action-condition-format") {
				SHEET_API.showSidebarBtnWin(SHEET_API_HD, "condition");
			} else if (itemId == "testAPI-action-picture-panel") {
				SHEET_API.showSidebarBtnWin(SHEET_API_HD, "picture");
			} else if (itemId == "testAPI-action-chart-panel") {
				SHEET_API.showSidebarBtnWin(SHEET_API_HD, "chart");
			} else if (itemId == "testAPI-action-background-panel") {
				SHEET_API.insertBackgroundImage(SHEET_API_HD);
			} else if (itemId == "testAPI-action-insert-page-break") {
				SHEET_API.insertPageBreak(SHEET_API_HD);
			} else if (itemId == "testAPI-action-delete-page-break") {
				SHEET_API.deletePageBreak(SHEET_API_HD);
			} else if (itemId == "testAPI-action-insert-comment") {
				SHEET_API.insertComment(SHEET_API_HD);
			} else if (itemId == "testAPI-action-insert-comment2") {
				SHEET_API.insertCommentDirect(SHEET_API_HD, "insert comment @ B2", 0, 2, 2);
			} else if (itemId == "testAPI-action-sum") {
				SHEET_API.sum(SHEET_API_HD);
			} else if (itemId == "testAPI-action-average") {
				SHEET_API.average(SHEET_API_HD);
			} else if (itemId == "testAPI-action-count") {
				SHEET_API.count(SHEET_API_HD);
			} else if (itemId == "testAPI-action-max") {
				SHEET_API.maxValue(SHEET_API_HD);
			} else if (itemId == "testAPI-action-min") {
				SHEET_API.minValue(SHEET_API_HD);
			} else if (itemId == "testAPI-action-dropList") {
				SHEET_API.insertDropList(SHEET_API_HD);
			} else if (itemId == "testAPI-action-checkbox") {
				SHEET_API.insertCheckbox(SHEET_API_HD);
			} else if (itemId == "testAPI-action-radio") {
				SHEET_API.insertRadio(SHEET_API_HD);
			} else if (itemId == "testAPI-action-datepicker") {
				SHEET_API.insertDatePicker(SHEET_API_HD);
			} else if (itemId == "testAPI-action-clear-item") {
				SHEET_API.clearItem(SHEET_API_HD);
			} else if (itemId == "testAPI-action-name-range") {
				SHEET_API.nameRange(SHEET_API_HD);
			} else if (itemId == "testAPI-action-name-range-update-address") {
				var result = SHEET_API.updateNamedRangeAddress(SHEET_API_HD, "testnmg", undefined, "Sheet1!$A$1:$A$2");
				if (result != true) {
					alert(result.msg);
				}
			} else if (itemId == "testAPI-action-name-range-update-comment") {
				var result = SHEET_API.updateNamedRangeComment(SHEET_API_HD, "testnmg", undefined, "comments");
				if (result != true) {
					alert(result.msg);
				}
			} else if (itemId == "testAPI-action-insert-hyperlink") {
				SHEET_API.insertHyperlink(SHEET_API_HD);
			} else if (itemId == "testAPI-action-insert-function") {
				SHEET_API.insertFormula(SHEET_API_HD);
			} else if (itemId == "testAPI-action-refresh-formula-function") {
				this.centralPanel.formulaRefreshExample();
			} else if (itemId == "testAPI-action-mathmatics-function") {
				var json = SHEET_API.functionListMathmatics();
				var count = json.length;
				var result = "Mathmatics:";
				for(var i = 0; i < count; i++){
					result += json[i].name + ",";
				}
				result = result.substr(0, result.length - 1);
				result += "===total:" + count;
				alert(result);
			} else if (itemId == "testAPI-action-logic-function") {
				var json = SHEET_API.functionListLogic();
				var count = json.length;
				var result = "Logic:";
				for(var i = 0; i < count; i++){
					result += json[i].name + ",";
				}
				result = result.substr(0, result.length - 1);
				result += "===total:" + count;
				alert(result);
			} else if (itemId == "testAPI-action-lookup-function") {
				var json = SHEET_API.functionListLookup();
				var count = json.length;
				var result = "Lookup:";
				for(var i = 0; i < count; i++){
					result += json[i].name + ",";
				}
				result = result.substr(0, result.length - 1);
				result += "===total:" + count;
				alert(result);
			} else if (itemId == "testAPI-action-statistical-function") {
				var json = SHEET_API.functionListStatistical();
				var count = json.length;
				var result = "Statistical:";
				for(var i = 0; i < count; i++){
					result += json[i].name + ",";
				}
				result = result.substr(0, result.length - 1);
				result += "===total:" + count;
				alert(result);
			} else if (itemId == "testAPI-action-engineering-function") {
				var json = SHEET_API.functionListEngineering();
				var count = json.length;
				var result = "Engineering:";
				for(var i = 0; i < count; i++){
					result += json[i].name + ",";
				}
				result = result.substr(0, result.length - 1);
				result += "===total:" + count;
				alert(result);
			} else if (itemId == "testAPI-action-compatibility-function") {
				var json = SHEET_API.functionListCompatibility();
				var count = json.length;
				var result = "Compatibility:";
				for(var i = 0; i < count; i++){
					result += json[i].name + ",";
				}
				result = result.substr(0, result.length - 1);
				result += "===total:" + count;
				alert(result);
			} else if (itemId == "testAPI-action-finicial-function") {
				var json = SHEET_API.functionListFinancial();
				var count = json.length;
				var result = "Financial:";
				for(var i = 0; i < count; i++){
					result += json[i].name + ",";
				}
				result = result.substr(0, result.length - 1);
				result += "===total:" + count;
				alert(result);
			} else if (itemId == "testAPI-action-string-function") {
				var json = SHEET_API.functionListText();
				var count = json.length;
				var result = "Text:";
				for(var i = 0; i < count; i++){
					result += json[i].name + ",";
				}
				result = result.substr(0, result.length - 1);
				result += "===total:" + count;
				alert(result);
			} else if (itemId == "testAPI-action-date-function") {
				var json = SHEET_API.functionListDate();
				var count = json.length;
				var result = "Date:";
				for(var i = 0; i < count; i++){
					result += json[i].name + ",";
				}
				result = result.substr(0, result.length - 1);
				result += "===total:" + count;
				alert(result);
			} else if (itemId == "testAPI-action-info-function") {
				var json = SHEET_API.functionListInformation();
				var count = json.length;
				var result = "Information:";
				for(var i = 0; i < count; i++){
					result += json[i].name + ",";
				}
				result = result.substr(0, result.length - 1);
				result += "===total:" + count;
				alert(result);
			} else if (itemId == "testAPI-action-get-style") {
				var json = SHEET_API.conditionStyleStore();
				var count = json.length;
				var result = "Style:";
				for(var i = 0; i < count; i++){
					result += json[i].text + ",";
				}
				result = result.substr(0, result.length - 1);
				result += "===total:" + count;
				alert(result);
			} else if (itemId == "testAPI-action-get-date-option") {
				var json = SHEET_API.conditionDateOptionStore();
				var count = json.length;
				var result = "Date:";
				for(var i = 0; i < count; i++){
					result += json[i].text + ",";
				}
				result = result.substr(0, result.length - 1);
				result += "===total:" + count;
				alert(result);
			} else if (itemId == "testAPI-action-get-repeat") {
				var json = SHEET_API.conditionRepeatStore();
				var count = json.length;
				var result = "Repeat:";
				for(var i = 0; i < count; i++){
					result += json[i].text + ",";
				}
				result = result.substr(0, result.length - 1);
				result += "===total:" + count;
				alert(result);
			}
			else if (itemId == "testAPI-action-greater-condition") {
				var json = SHEET_API.conditionStyleStore();
				SHEET_API.conditionGreater(SHEET_API_HD, 10, json[3].style);
			}
			else if (itemId == "testAPI-action-less-condition") {
				var json = SHEET_API.conditionStyleStore();
				SHEET_API.conditionLess(SHEET_API_HD, 10, json[3].style);
			}
			else if (itemId == "testAPI-action-equal-condition") {
				var json = SHEET_API.conditionStyleStore();
				SHEET_API.conditionEqual(SHEET_API_HD, 10, json[3].style);
			}
			else if (itemId == "testAPI-action-between-condition") {
				var json = SHEET_API.conditionStyleStore();
				SHEET_API.conditionBetween(SHEET_API_HD, 10, 20, json[3].style);
			}
			else if (itemId == "testAPI-action-include-condition") {
				var json = SHEET_API.conditionStyleStore();
				SHEET_API.conditionInclude(SHEET_API_HD, 10, json[3].style);
			}
			else if (itemId == "testAPI-action-date-condition") {
				var json = SHEET_API.conditionStyleStore();
				var date = SHEET_API.conditionDateOptionStore();
				SHEET_API.conditionDate(SHEET_API_HD, date[0].id, json[3].style);
			}
			else if (itemId == "testAPI-action-repeat-condition") {
				var json = SHEET_API.conditionStyleStore();
				SHEET_API.conditionRepeat(SHEET_API_HD, 0, json[3].style);
			}
			else if (itemId == "testAPI-action-above-condition") {
				var json = SHEET_API.conditionStyleStore();
				SHEET_API.conditionAbove(SHEET_API_HD, json[3].style);
			}
			else if (itemId == "testAPI-action-below-condition") {
				var json = SHEET_API.conditionStyleStore();
				SHEET_API.conditionBelow(SHEET_API_HD, json[3].style);
			}
			else if (itemId == "testAPI-action-max-condition") {
				var json = SHEET_API.conditionStyleStore();
				SHEET_API.conditionMax(SHEET_API_HD, 10, json[3].style);
			}
			else if (itemId == "testAPI-action-top-condition") {
				var json = SHEET_API.conditionStyleStore();
				SHEET_API.conditionTop(SHEET_API_HD, 10, json[3].style);
			}
			else if (itemId == "testAPI-action-min-condition") {
				var json = SHEET_API.conditionStyleStore();
				SHEET_API.conditionMin(SHEET_API_HD, 10, json[3].style);
			}
			else if (itemId == "testAPI-action-bottom-condition") {
				var json = SHEET_API.conditionStyleStore();
				SHEET_API.conditionBottom(SHEET_API_HD, 10, json[3].style);
			}
			else if (itemId == "testAPI-action-colorbar-condition") {
				SHEET_API.conditionColorBar(SHEET_API_HD, [255,128,0]);
			}
			else if (itemId == "testAPI-action-colorgrad-condition") {
				SHEET_API.conditionColorChange(SHEET_API_HD, [255,0,0], [0,0,255], [0,255,0]);
			}
			else if (itemId == "testAPI-action-iconset-condition") {
				SHEET_API.conditionIconSet(SHEET_API_HD, 0);
			}
			
			else if (itemId == "testAPI-general-getJsonData") {
				var json = SHEET_API.getJsonData(SHEET_API_HD);
				console.log(json);
				alert("See console for details");
			} 		
			else if (itemId == "testAPI-general-getAllRangeMeTreeRefered") {				
				var json = this.centralPanel.getAllRangeMeTreeRefered(itemId, titleTxt, exampleJson);
			}
			else if (itemId == "testAPI-general-crossFileRef") {				
				var json = this.centralPanel.getCrossFileRef();
			}		
			
			else if (itemId == "testAPI-action-find-text") {
				this.store = SHEET_API.searchFindMatch(SHEET_API_HD, 11);
			}
			else if (itemId == "testAPI-action-prev-text") {
				SHEET_API.searchPrevMatch(SHEET_API_HD, this.store);
			}
			else if (itemId == "testAPI-action-next-text") {
				SHEET_API.searchNextMatch(SHEET_API_HD, this.store);
			}
			else if (itemId == "testAPI-action-replace-select") {
				SHEET_API.searchReplaceSelect(SHEET_API_HD, "22", "11");
			}
			else if (itemId == "testAPI-action-replace-all") {
				SHEET_API.searchReplaceAll(SHEET_API_HD, "22", "11");
				//SHEET_API.insertComment(SHEET_API_HD, "aaaaaaa", 8, 4);
			}
			
			else if (itemId == "testAPI-action-insert-picture") {
				SHEET_API.pictureInsert(SHEET_API_HD, "22", false);
			}
			
			
			else if (itemId == "testAPI-data-validation") {
				SHEET_API.showValidation(SHEET_API_HD);
			}
			else if (itemId == "testAPI-data-sort-asc") {
				SHEET_API.sortCellByAsc(SHEET_API_HD);
			}
			else if (itemId == "testAPI-data-sort-desc") {
				SHEET_API.sortCellByDesc(SHEET_API_HD);
			}
			else if (itemId == "testAPI-data-filter") {
				SHEET_API.filter(SHEET_API_HD);
			}
			else if (itemId == "testAPI-data-delete-repeat") {
				SHEET_API.deleteRepeatItem(SHEET_API_HD);
			}
			else if (itemId == "testAPI-data-clean") {
				SHEET_API.clean(SHEET_API_HD);
			}
			else if (itemId == "testAPI-data-clean-content") {
				SHEET_API.cleanContent(SHEET_API_HD);
			}
			else if (itemId == "testAPI-data-clean-style") {
				SHEET_API.cleanStyle(SHEET_API_HD);
			}
			
			else if (itemId == "testAPI-view-freeze") {
				SHEET_API.toggleFreeze(SHEET_API_HD);
			} 
			else if (itemId == "testAPI-view-split") {
				SHEET_API.toggleSplit(SHEET_API_HD);
			} 
			else if (itemId == "testAPI-view-toggleColumn") {
				SHEET_API.toggleColumnName(SHEET_API_HD);
			} 
			else if (itemId == "testAPI-view-toggleRow") {
				SHEET_API.toggleRowName(SHEET_API_HD);
			} 
			else if (itemId == "testAPI-view-toggleGrid") {
				SHEET_API.toggleGridLine(SHEET_API_HD, true);
			}
			else if (itemId == "testAPI-view-zoom") {
				SHEET_API.zoom(SHEET_API_HD, 1.25);
			}
			else if (itemId == "testAPI-view-lock-all") {
				SHEET_API.toggleEditable(SHEET_API_HD);
			}
			else if (itemId == "testAPI-view-lock-other") {
				SHEET_API.toggleEditableForOther(SHEET_API_HD);
			}
			
		}
		
		else if (itemId) {
		    this.centralPanel.updateSheet(itemId, titleTxt, exampleJson);
		}
		
		if(exampleCode){
			JSON_DATA[exampleCode]();
		}
	},
	
	/**
	 * contextmenu event
	 */
	onContextMenu : function(view, record, item, index, event) {
		var itemId = record.data.id, docHtml = record.raw.docUrl, exampleJson = record.raw.exampleJson;
		
		if (itemId && docHtml && exampleJson) {
			this.docUrl = docHtml;
			this.srcHtml = record.raw.exampleJson + '.html';
			this.treeMenu.showAt(event.getXY());
	        event.stopEvent();
		} else if (itemId && docHtml) {
			this.docUrl = docHtml;
			this.treeMenu2.showAt(event.getXY());
	        event.stopEvent();
		}
	}
});
