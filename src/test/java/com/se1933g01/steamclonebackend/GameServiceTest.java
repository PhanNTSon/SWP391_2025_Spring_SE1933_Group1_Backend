@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    @Test
void getGamesUnder5_shouldReturnMappedDTOs() {
    // Arrange
    Media media = new Media(1L, "http://img.png", MediaType.IMAGE);
    Tag tag = new Tag(1L, "RPG");

    Game game = new Game();
    game.setGameId(100L);
    game.setName("Cheap Game");
    game.setPrice(4.99);
    game.setShortDescription("Nice game");
    game.setMedia(List.of(media));
    game.setTags(List.of(tag));

    when(gameRepository.findTop10PriceUnder5(any(PageRequest.class)))
            .thenReturn(List.of(game));

    // Act
    List<GamePresentDTO> result = gameService.getGamesUnder5();

    // Assert
    assertThat(result).hasSize(1);

    GamePresentDTO dto = result.get(0);
    assertThat(dto.getGameId()).isEqualTo(100L);
    assertThat(dto.getName()).isEqualTo("Cheap Game");
    assertThat(dto.getPrice()).isEqualTo(4.99);
    assertThat(dto.getShortDescription()).isEqualTo("Nice game");

    // Media mapping
    assertThat(dto.getMedia()).hasSize(1);
    MediaDTO mediaDTO = dto.getMedia().get(0);
    assertThat(mediaDTO.getMediaId()).isEqualTo(1L);
    assertThat(mediaDTO.getUrl()).isEqualTo("http://img.png");
    assertThat(mediaDTO.getType()).isEqualTo(MediaType.IMAGE);

    // Tag mapping
    assertThat(dto.getTags()).hasSize(1);
    TagDTO tagDTO = dto.getTags().get(0);
    assertThat(tagDTO.getTagId()).isEqualTo(1L);
    assertThat(tagDTO.getTagName()).isEqualTo("RPG");

    // Verify repository call
    verify(gameRepository).findTop10PriceUnder5(any(PageRequest.class));
}

}
