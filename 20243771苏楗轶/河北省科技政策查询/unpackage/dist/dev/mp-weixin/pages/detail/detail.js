"use strict";
const common_vendor = require("../../common/vendor.js");
const API_BASE = "http://10.39.60.209:8080/api";
const _sfc_main = /* @__PURE__ */ common_vendor.defineComponent({
  __name: "detail",
  setup(__props) {
    const policy = common_vendor.ref(null);
    const policyId = common_vendor.ref("");
    const loadPolicyDetail = () => {
      const url = `${API_BASE}/policy/${policyId.value}`;
      common_vendor.index.__f__("log", "at pages/detail/detail.uvue:77", `发起API请求: ${url}`);
      common_vendor.index.request({
        url,
        method: "GET",
        header: new UTSJSONObject({
          "Content-Type": "application/json",
          "Accept": "application/json"
        }),
        timeout: 1e4,
        success: (res = null) => {
          common_vendor.index.__f__("log", "at pages/detail/detail.uvue:88", `API请求成功: ${url}`, res);
          if (res.statusCode === 200) {
            if (res.data && res.data.code === 200) {
              policy.value = res.data.data;
            } else {
              common_vendor.index.showToast({
                title: "政策不存在",
                icon: "none"
              });
            }
          } else {
            common_vendor.index.__f__("error", "at pages/detail/detail.uvue:99", `API请求失败(${res.statusCode}): ${url}`, res);
            common_vendor.index.showToast({
              title: `请求失败: ${res.statusCode}`,
              icon: "none"
            });
          }
        },
        fail: (err = null) => {
          common_vendor.index.__f__("error", "at pages/detail/detail.uvue:107", `API请求异常: ${url}`, err);
          common_vendor.index.showToast({
            title: `请求失败: ${err.errMsg ? err.errMsg : "网络错误"}`,
            icon: "none"
          });
        }
      });
    };
    const formatDate = (date = null) => {
      if (date === null || date === void 0 || date === "")
        return "";
      const d = new Date(date);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
    };
    const goBack = () => {
      common_vendor.index.navigateBack();
    };
    common_vendor.onMounted(() => {
      var _a, _b;
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      policyId.value = (_b = (_a = currentPage === null || currentPage === void 0 ? null : currentPage.options) === null || _a === void 0 ? null : _a.id) !== null && _b !== void 0 ? _b : "";
      if (policyId.value !== null && policyId.value !== void 0 && policyId.value !== "") {
        loadPolicyDetail();
      }
    });
    return (_ctx, _cache) => {
      "raw js";
      const __returned__ = common_vendor.e({
        a: policy.value
      }, policy.value ? {
        b: common_vendor.t(policy.value.name),
        c: common_vendor.t(policy.value.document || "无"),
        d: common_vendor.t(policy.value.organ),
        e: common_vendor.t(formatDate(policy.value.pubdata)),
        f: common_vendor.t(formatDate(policy.value.perdata)),
        g: common_vendor.t(policy.value.type),
        h: common_vendor.t(policy.value.range || "无"),
        i: common_vendor.t(policy.value.state || "无"),
        j: common_vendor.n(policy.value.state === "有效" ? "valid" : "invalid"),
        k: common_vendor.t(policy.value.keyword || "无"),
        l: common_vendor.t(policy.value.text)
      } : {}, {
        m: common_vendor.o(goBack),
        n: common_vendor.sei(common_vendor.gei(_ctx, ""), "view")
      });
      return __returned__;
    };
  }
});
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/detail/detail.js.map
