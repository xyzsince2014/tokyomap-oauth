package tokyomap.oauth.services;

import tokyomap.oauth.common.ApiConstants;
import tokyomap.oauth.common.ApiConstants.REDIRECT_FLG;
import tokyomap.oauth.dtos.ApiReturnInfoDto;
import tokyomap.oauth.dtos.RequestBaseInfoDto;
import tokyomap.oauth.dtos.RequestIndexDto;
import tokyomap.oauth.dtos.ResponseBaseInfoDto;
import tokyomap.oauth.dtos.ResponseIndexDto;

public class IndexServiceImpl extends ApiBaseServiceImpl implements IndexService {

//  @Resource
//  IndexLogic indexLogic;

  /**
   * hoge fuga
   * @param requestDto request info
   * @return response info
   * @throws Exception error in service or logic
   *         other Exception: system error
   */
  @Override
  protected ResponseBaseInfoDto doExecution(RequestBaseInfoDto requestDto) throws Exception {
    RequestIndexDto requestIndexDto = (RequestIndexDto) requestDto;

    // the return info
    ApiReturnInfoDto apiReturnInfoDto = new ApiReturnInfoDto();
    apiReturnInfoDto.setStatusCode(ApiConstants.SUCCESS);
    apiReturnInfoDto.setReidrectFlg(REDIRECT_FLG.NOT_REDIRECT.getFlg());

//    RequestIndexDto modifiedRequestDto = this.indexLogic.hoge();
    RequestIndexDto modifiedRequestDto = null;

    // the resposne info
    ResponseIndexDto responseDto = getResponseDto(apiReturnInfoDto, modifiedRequestDto);

    return responseDto;
  }

  @Override
  protected ResponseIndexDto getResponseDto(ApiReturnInfoDto apiReturnInfo, RequestBaseInfoDto requestDto) {
    ResponseIndexDto responseDto = new ResponseIndexDto();
    responseDto.setApiReturnInfo(apiReturnInfo);
    responseDto.setDataInfo((RequestIndexDto) requestDto);
    return responseDto;
  }
}
