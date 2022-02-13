package tokyomap.oauth.services;

import tokyomap.oauth.dtos.RequestBaseInfoDto;
import tokyomap.oauth.dtos.ResponseBaseInfoDto;

public interface IndexService {

  /** the path of the endpoint */
  String ENDPOINT = "/api/index/";

  /**
   * execution
   * equivalent to `public abstract ResponseIndexDto execute(RequestBaseInfoDto requestDto, String contextPath);`
   * @param requestDto request info
   * @param contextPath context path
   * @return ResponseBaseInfoDto
   */
  ResponseBaseInfoDto execute(RequestBaseInfoDto requestDto, String contextPath);
}
